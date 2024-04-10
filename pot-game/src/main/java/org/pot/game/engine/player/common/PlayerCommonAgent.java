package org.pot.game.engine.player.common;

import com.google.protobuf.ByteString;
import lombok.Getter;
import org.pot.game.engine.enums.PlayerImageTypeEnum;
import org.pot.game.engine.enums.PlayerImageUnlockType;
import org.pot.game.engine.enums.StatisticsEnum;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.reward.ResourceReward;
import org.pot.game.persistence.entity.PlayerCommonEntity;
import org.pot.game.resource.enums.ResourceType;
import org.pot.game.resource.parameter.GameParameters;
import org.pot.game.resource.player.PlayerProfile;
import org.pot.game.resource.player.PlayerProfileConfig;
import org.pot.message.protocol.commander.*;
import org.pot.message.protocol.login.LoginRespS2C;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class PlayerCommonAgent extends PlayerAgentAdapter {
    private final PlayerCommonInfo playerCommonInfo = new PlayerCommonInfo();

    public PlayerCommonAgent(Player player) {
        super(player);
    }

    protected void initData(PlayerData playerData) {
        this.playerCommonInfo.init();
        PlayerCommonEntity entity = new PlayerCommonEntity();
        entity.setId(this.player.getUid());
        this.playerCommonInfo.update(entity);
        playerData.setCommonEntity(entity);
    }

    protected void loadData(PlayerData playerData) {
        if (playerData.getCommonEntity() == null) {
            initData(playerData);
            return;
        }
        this.playerCommonInfo.initData(playerData.getCommonEntity());
    }

    public int getLevel() {
        return this.playerCommonInfo.getLevel();
    }

    protected void saveData(PlayerData playerData) {
        PlayerCommonEntity playerCommonEntity = new PlayerCommonEntity();
        this.playerCommonInfo.update(playerCommonEntity);
        playerCommonEntity.setId(this.player.getUid());
        playerData.setCommonEntity(playerCommonEntity);
    }

    protected void onLogin(LoginRespS2C.Builder loginDataBuilder) {
        loginDataBuilder.setCommonInfo(builderCommonInfo());
    }

    public CommonInfo builderCommonInfo() {
        CommonInfo.Builder builder = CommonInfo.newBuilder();
        builder.setPlayerId(this.player.getUid());
        builder.setLevel(this.playerCommonInfo.getLevel());
        builder.setExp(this.playerCommonInfo.getExp());
        builder.setName(this.player.getName());
        builder.setServer(String.valueOf(this.player.getProfile().getServerId()));
        builder.setTotalAbility(this.player.powerComponent.getPower());
        builder.setImageInfo(builderImageInfo());
        builder.setEnergyInfo(builderEnergyInfo());
        builder.setPowerInfo(builderPowerInfo());
        builder.setLanguage(this.player.getProfile().getLanguage());
        builder.putAllStatistics(StatisticsEnum.needSendClient(this.playerCommonInfo.getPlayerStatistics().getVarMap()));
        return builder.build();
    }

    public PowerInfo builderPowerInfo() {
        PowerInfo.Builder powerBuilder = PowerInfo.newBuilder();
        powerBuilder.setNextTime(this.playerCommonInfo.getPowerRefreshTime());
        powerBuilder.setAllTime(getAllPowerTime());
        powerBuilder.setCount(this.player.resourceAgent.getResource(ResourceType.POWER).getCount());
        return powerBuilder.build();
    }

    private long getAllPowerTime() {
        long limitValue = GameParameters.power_limit.getLong();
        ResourceReward resource = this.player.resourceAgent.getResource(ResourceType.POWER);
        long value = limitValue - resource.getCount();
        if (value > 0L) {
            long recoveryTime = GameParameters.recovery_power_time.getLong();
            double costValue = 0;
            recoveryTime = (int) (recoveryTime / (1.0D + costValue));
            return this.playerCommonInfo.getPowerRefreshTime() + recoveryTime * value;
        }
        return 0L;
    }

    public EnergyInfo builderEnergyInfo() {
        EnergyInfo.Builder energyBuilder = EnergyInfo.newBuilder();
        energyBuilder.setNextTime(this.playerCommonInfo.getEnergyRefreshTime());
        energyBuilder.setAllTime(getAllEnergyTime());
        energyBuilder.setCount(this.player.resourceAgent.getResource(ResourceType.ENERGY).getCount());
        return energyBuilder.build();
    }

    private long getAllEnergyTime() {
        int limitValue = GameParameters.energy_limit.getInt();
        ResourceReward resource = this.player.resourceAgent.getResource(ResourceType.ENERGY);
        int value = limitValue - (int) resource.getCount();
        if (value > 0) {
            int recoveryTime = GameParameters.recovery_energy_time.getInt();
            return this.playerCommonInfo.getEnergyRefreshTime() + (long) recoveryTime * value;
        }
        return 0L;
    }

    public ImageInfo builderImageInfo() {
        ImageInfo.Builder builder = ImageInfo.newBuilder();
        builder.setIconId(this.playerCommonInfo.getIconId());
        builder.setFrameId(this.playerCommonInfo.getFrameId());
        builder.setAvatar(ByteString.copyFrom(this.playerCommonInfo.getPicture()));
        builder.setAvatarTime(this.playerCommonInfo.getPictureRefreshTime());
        Map<Integer, PlayerIcon> playerIconMap = this.playerCommonInfo.getPlayerIconMap();
        playerIconMap.values().forEach(playerIcon -> builder.addIcons(builderUnLockIcon(playerIcon)));
        Map<Integer, PlayerFrame> playerFrameMap = this.playerCommonInfo.getPlayerFrameMap();
        playerFrameMap.values().forEach(playerFrame -> builder.addFrames(builderUnLockFrame(playerFrame)));
        return builder.build();
    }

    public UnLookIconInfoS2C builderUnLockIcon(PlayerIcon playerIcon) {
        UnLookIconInfoS2C.Builder lookInfoBuilder = UnLookIconInfoS2C.newBuilder();
        lookInfoBuilder.setLookId(playerIcon.getIconId());
        lookInfoBuilder.setExpireTime(playerIcon.getTime());
        return lookInfoBuilder.build();
    }

    public UnLockFrameInfoS2C builderUnLockFrame(PlayerFrame playerFrame) {
        UnLockFrameInfoS2C.Builder frameInfoBuilder = UnLockFrameInfoS2C.newBuilder();
        frameInfoBuilder.setFrameId(playerFrame.getFrameId());
        frameInfoBuilder.setExpireTime(playerFrame.getTime());
        return frameInfoBuilder.build();
    }

    public void unlockProfile(int unlockValue, PlayerImageUnlockType unlockType) {
        List<PlayerProfile> listByValue = PlayerProfileConfig.getInstance().getListByValue(unlockValue, unlockType.getType());
        listByValue.forEach(playerProfile -> addIconOrFrame(playerProfile.getId(), -1));
    }

    public void addIconOrFrame(int id, int time) {
        PlayerProfile spec = PlayerProfileConfig.getInstance().getSpec(id);
        if (spec == null)
            return;
        if (PlayerImageTypeEnum.FRAME.getType() == spec.getType()) {
            Map<Integer, PlayerFrame> playerFrameMap = this.playerCommonInfo.getPlayerFrameMap();
            PlayerFrame playerFrame = playerFrameMap.computeIfAbsent(id, k -> new PlayerFrame());
            playerFrame.setFrameId(id);
            if (time == -1 || spec.getTimeType() == 0) {
                playerFrame.setTime(-1L);
            } else if (playerFrame.getTime() != -1L) {
                playerFrame.setTime(playerFrame.getTime() + TimeUnit.DAYS.toMillis(time));
            }
            sendMessage(builderUnLockFrame(playerFrame));
        } else {
            Map<Integer, PlayerIcon> playerIconMap = this.playerCommonInfo.getPlayerIconMap();
            PlayerIcon playerIcon = playerIconMap.computeIfAbsent(id, k -> new PlayerIcon());
            playerIcon.setIconId(id);
            if (time == -1 || spec.getTimeType() == 0) {
                playerIcon.setTime(-1L);
            } else if (playerIcon.getTime() != -1L) {
                playerIcon.setTime(playerIcon.getTime() + TimeUnit.DAYS.toMillis(time));
            }
            sendMessage(builderUnLockIcon(playerIcon));
        }
    }
}
