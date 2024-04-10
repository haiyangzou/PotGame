package org.pot.game.engine.player.module.hero;

import lombok.Getter;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.util.ExDateTimeUtil;
import org.pot.game.engine.enums.*;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.engine.reward.ResourceRewardUtil;
import org.pot.game.engine.reward.resource.ItemResourceReward;
import org.pot.game.persistence.entity.PlayerHeroEntity;
import org.pot.game.resource.common.Drop;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.enums.ResourceType;
import org.pot.game.resource.hero.HeroInfo;
import org.pot.game.resource.hero.HeroInfoConfig;
import org.pot.game.resource.item.ItemChange;
import org.pot.game.resource.item.ItemChangeConfig;
import org.pot.game.resource.recruit.CardsDraw;
import org.pot.game.resource.recruit.CardsDrawConfig;
import org.pot.game.resource.recruit.CardsPool;
import org.pot.game.resource.recruit.CardsPoolConfig;
import org.pot.game.util.PlayerDropUtil;
import org.pot.message.protocol.hero.HeroDrawRewardS2C;
import org.pot.message.protocol.hero.HeroDrawView;
import org.pot.message.protocol.hero.PlayerHeroDto;
import org.pot.message.protocol.hero.PushHeroInfoS2C;
import org.pot.message.protocol.login.LoginRespS2C;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerHeroAgent extends PlayerAgentAdapter {
    private Map<Integer, PlayerHero> playerHeroes = new ConcurrentHashMap<>();
    private Map<Integer, CardsPoolInfo> pubInfoMap = new ConcurrentHashMap<>();

    public PlayerHeroAgent(Player player) {
        super(player);
    }

    protected void initData(PlayerData playerData) {
        PlayerHeroEntity playerHeroEntity = new PlayerHeroEntity();
        playerHeroEntity.setId(this.player.getUid());
        playerHeroEntity.setHeroInfo(new ArrayList<>());
        playerData.setHeroEntity(playerHeroEntity);
    }

    protected void loadData(PlayerData playerData) {
        PlayerHeroEntity playerHeroEntity = playerData.getHeroEntity();
        List<PlayerHero> playerHeroList = playerHeroEntity.getHeroInfo();
        for (PlayerHero playerHero : playerHeroList) {
            this.playerHeroes.put(playerHero.getHeroBaseId(), playerHero);
        }
        this.pubInfoMap = new ConcurrentHashMap<>(playerHeroEntity.getPubInfoMap());
    }

    protected void saveData(PlayerData playerData) {
        PlayerHeroEntity playerHeroEntity = new PlayerHeroEntity();
        playerHeroEntity.setId(this.player.getUid());
        playerHeroEntity.setHeroInfo(new ArrayList<>(this.playerHeroes.values()));
        playerData.setHeroEntity(playerHeroEntity);
    }

    protected void onLogin(LoginRespS2C.Builder loginDataBuilder) {
        loginDataBuilder.addAllHeros(getPlayerHeroDtoList());
    }

    /**
     * 蛋爆出来宠物
     *
     * @param way
     * @return
     */
    public IErrorCode drawHero(int way) {
        CardsDraw cardsDraw = CardsDrawConfig.getInstance().getSpec(way);
        if (cardsDraw == null)
            return GameErrorCode.BASE_DATA_NOT_EXIST;
        CardsPool cardsPool = CardsPoolConfig.getInstance().getSpec(cardsDraw.getPoolId());
        if (cardsPool == null)
            return GameErrorCode.BASE_DATA_NOT_EXIST;
        CardsPoolInfo cardsPoolInfo = this.pubInfoMap.get(cardsPool.getId());
        String itemId = "";
        long num = 0L;
        if (cardsDraw.getType() == DrawType.FREE.ordinal()) {
            if (cardsPoolInfo.getFreeCount() >= cardsPool.getFreeCount())
                return GameErrorCode.HERO_NO_FREE_COUNT;
            long now = System.currentTimeMillis();
            if (cardsPoolInfo.getNextFreeTime() > now)
                return GameErrorCode.HERO_FREE_TIME_NOT_ENOUGH;
            cardsPoolInfo.setFreeCount(cardsPoolInfo.getFreeCount() + 1);
            long nextDayStart = ExDateTimeUtil.getNextDayStart();
            if (cardsPoolInfo.getFreeCount() >= cardsPool.getFreeCount()) {
                if (cardsPool.getFreeRefreshType() == DrawRefreshType.INITIAL_FREE.getType()) {
                    cardsPoolInfo.setNextFreeTime(nextDayStart);
                } else {
                    cardsPoolInfo.setNextFreeTime(now + cardsPool.getFreeTime());
                }
            } else if (cardsPool.getFreeRefreshType() == DrawRefreshType.INITIAL_FREE.getType()) {
                cardsPoolInfo.setNextFreeTime(Math.min(now + cardsPool.getFreeTime(), nextDayStart));
            } else {
                cardsPoolInfo.setNextFreeTime(now + cardsPool.getFreeTime());
            }
        } else {
            //消耗元宝
            IErrorCode iErrorCode = ResourceRewardUtil.checkAndExecute(this.player, RewardSourceType.DRAW_HERO, false, cardsDraw.getCost());
            if (iErrorCode != null)
                return iErrorCode;
            Reward reward = cardsDraw.getCost();
            itemId = reward.getId();
            num = Math.abs(reward.getCount());
        }
        long oldScore = this.player.resourceAgent.getResource(ResourceType.HERO_SCORE).getCount();
        if (cardsDraw.getScore() > 0L) {
            ResourceReward resourceReward = new ResourceReward(ResourceType.HERO_SCORE, cardsDraw.getScore());
            ResourceRewardUtil.executeResourceReward(this.player, RewardSourceType.DRAW_HERO, resourceReward);
        }
        List<Reward> rewardList;
        if (cardsDraw.getFirstDraw() == 1 && cardsPoolInfo.isFirstDraw()) {
            Drop[] firstDrop = cardsDraw.getFirstDrop();
            rewardList = PlayerDropUtil.randomDrop(this.player, firstDrop);
        } else {
            Drop drop = cardsDraw.getDrop();
            rewardList = PlayerDropUtil.randomDrop(this.player, drop);
        }
        if (cardsPoolInfo.isFirstDraw() && cardsDraw.getFirstDraw() == 1)
            cardsPoolInfo.setFirstDraw(false);
        ResourceRewardUtil.executeReward(this.player, RewardSourceType.HERO_DRAW, rewardList.<Reward>toArray(new Reward[0]));
        List<HeroDrawView> heroDrawViews = new ArrayList<>();
        for (Reward reward : rewardList)
            heroDrawViews.add(getHeroDrawView(reward));
        if (cardsDraw.getType() == 1) {
            this.player.statisticsComponent.addStatisticsLong(StatisticsServerEnum.HERO_DRAW_TYPE_1, cardsDraw.getDrawCount());
        } else if (cardsDraw.getType() == 2) {
            this.player.statisticsComponent.addStatisticsLong(StatisticsServerEnum.HERO_DRAW_TYPE_2, cardsDraw.getDrawCount());
        } else {
            this.player.statisticsComponent.addStatisticsLong(StatisticsServerEnum.HERO_DRAW_TYPE_3, cardsDraw.getDrawCount());
        }
//        this.player.eventComponent.postPlayerEvent(HeroRecruit.builder().type((Integer) cardsPool.getId()).count(cardsDraw.getDrawCount()).build());
//        if (cardsDraw.getDrawCount() == 10)
//            this.player.eventComponent.postPlayerEvent(HeroRecruitTen.builder().build());
        HeroDrawRewardS2C.Builder builder = HeroDrawRewardS2C.newBuilder();
        builder.addAllHeroDrawViews(heroDrawViews);
        builder.setScore(Long.valueOf(cardsDraw.getScore()).intValue());
        sendMessage(builder.build());
        return null;
    }

    public List<PlayerHeroDto> getPlayerHeroDtoList() {
        List<PlayerHeroDto> playerHeroList = new ArrayList<>();
        this.playerHeroes.values().forEach(playerHero -> playerHeroList.add(encapsulation(playerHero)));
        return playerHeroList;
    }

    public PlayerHeroDto encapsulation(PlayerHero playerHero) {
        PlayerHeroDto.Builder builder = PlayerHeroDto.newBuilder();
        builder.setHeroBaseId(playerHero.getHeroBaseId());
        builder.setEvolution(playerHero.getRank());
        builder.setHeroLevel(playerHero.getLevel());
        builder.setStrengthenLevel(playerHero.getStrengthenLevel());
        builder.setAbility(playerHero.getAbility());
        builder.setHeroStatus(playerHero.getHeroStatus());
        builder.setStar(playerHero.getStar());
        builder.setBackgroudReward(playerHero.getBackgroundReward());
        builder.setPlayerHeroEquipId(playerHero.getEquipId());
        builder.setSkin(playerHero.getSkin());
        if (playerHero.getEquipId() > 0) ;
        return builder.build();
    }

    private HeroDrawView getHeroDrawView(Reward reward) {
        HeroDrawView.Builder heroDrawViewBuilder = HeroDrawView.newBuilder();
        String itemId = reward.getId();
        ItemChange itemChange = ItemChangeConfig.getInstance().getSpec(itemId);
        if (itemChange != null) {
            heroDrawViewBuilder.setItemId(itemChange.getChangeId());
            heroDrawViewBuilder.setCount((int) reward.getCount() * itemChange.getChangePercent());
        } else {
            heroDrawViewBuilder.setItemId(reward.getId());
            heroDrawViewBuilder.setCount((int) reward.getCount());
        }
        HeroInfo heroInfo = HeroInfoConfig.getInstance().getHeroInfoByMedalId(itemId);
        if (heroInfo != null && reward.getCount() >= 10L) {
            heroDrawViewBuilder.setIsNewHero(!this.playerHeroes.containsKey(heroInfo.getId()));
            if (!this.playerHeroes.containsKey(heroInfo.getId()))
                activeHero(heroInfo.getId());
        } else {
            heroDrawViewBuilder.setIsNewHero(false);
        }
        return heroDrawViewBuilder.build();
    }

    public IErrorCode activeHero(int heroBaseId) {
        if (this.playerHeroes.containsKey(heroBaseId))
            return GameErrorCode.HERO_ACTIVE;
        HeroInfo heroInfo = getHeroConfig(heroBaseId);
        if (heroInfo == null)
            return GameErrorCode.BASE_DATA_NOT_EXIST;
        if (heroInfo.getMedalAmount() <= 0)
            return GameErrorCode.BASE_DATA_NOT_EXIST;
        ItemResourceReward itemResourceReward = new ItemResourceReward(heroInfo.getMedalId(), -heroInfo.getMedalAmount());
        IErrorCode iErrorCode = ResourceRewardUtil.checkAndExecute(this.player, RewardSourceType.ACTIVE_HERO, false, itemResourceReward);
        if (iErrorCode != null)
            return iErrorCode;
        getHero(heroInfo);
        this.player.powerComponent.agentPowerChange(this);
        return null;
    }

    private void getHero(HeroInfo heroInfo) {
        PlayerHero playerHero = new PlayerHero();
        playerHero.initHero(heroInfo);
        this.playerHeroes.put(heroInfo.getId(), playerHero);
        pushHeroInfo(playerHero);
//        this.player.eventComponent.postPlayerEvent(HeroGet.builder().build());
        this.player.commonAgent.unlockProfile(heroInfo.getProfileId(), PlayerImageUnlockType.HERO);
    }

    public void pushHeroInfo(PlayerHero playerHero) {
        PushHeroInfoS2C.Builder builder = PushHeroInfoS2C.newBuilder();
        builder.setPlayerHero(encapsulation(playerHero));
        sendMessage(builder.build());
    }

    private HeroInfo getHeroConfig(int heroBaseId) {
        return HeroInfoConfig.getInstance().getSpec(heroBaseId);
    }
}
