package org.pot.game.engine.player.module.hero;

import com.eyu.kylin.magics.protocol.hero.PlayerHeroDto;
import lombok.Getter;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.player.module.army.PlayerArmy;
import org.pot.game.persistence.entity.PlayerHeroEntity;
import org.pot.message.protocol.login.LoginRespS2C;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerHeroAgent extends PlayerAgentAdapter {
    private final Map<Integer, PlayerHero> playerHeroes = new ConcurrentHashMap<>();

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
        for (PlayerHero playerHero : playerHeroList)
            this.playerHeroes.put(playerHero.getHeroBaseId(), playerHero);
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
}
