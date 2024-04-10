package org.pot.game.engine.player.common;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.enums.PlayerImageTypeEnum;
import org.pot.game.engine.enums.PlayerImageUnlockType;
import org.pot.game.engine.player.module.common.PlayerStatistics;
import org.pot.game.persistence.entity.PlayerCommonEntity;
import org.pot.game.resource.parameter.GameParameters;
import org.pot.game.resource.player.PlayerProfile;
import org.pot.game.resource.player.PlayerProfileConfig;

import java.util.*;

@Getter
@Setter
public class PlayerCommonInfo {
    private int level = 1;
    private int exp;
    private int iconId;
    private int frameId;
    private byte[] picture;
    private long powerRefreshTime;
    private long energyRefreshTime;
    private long pictureRefreshTime;
    private long sendSysMailId = 0L;
    private PlayerStatistics playerStatistics = new PlayerStatistics();
    private Set<Long> forbiddenPlayerIds = new HashSet<>();
    private Map<Integer, PlayerFrame> playerFrameMap = new HashMap<>();

    public PlayerCommonInfo() {
    }

    public void initData(PlayerCommonEntity entity) {
        this.level = entity.getLevel();
        this.exp = entity.getExp();
        this.iconId = entity.getProfileIconId();
        this.frameId = entity.getProfileFrameId();
        this.picture = entity.getProfilePicture();
        this.powerRefreshTime = entity.getPowerRefreshTime();
        this.energyRefreshTime = entity.getEnergyRefreshTime();
        this.pictureRefreshTime = entity.getPictureRefreshTime();
        List<PlayerFrame> playerFrames = entity.getFrameDetail();
        playerFrames.forEach(playerFrame -> this.playerFrameMap.put(playerFrame.getFrameId(), playerFrame));
        List<PlayerIcon> playerIcons = entity.getIconDetail();
        playerIcons.forEach(playerIcon -> this.playerIconMap.put(playerIcon.getIconId(), playerIcon));
        this.playerStatistics.setVarMap(entity.getStatisticsDetail());
        this.playerFavoritesInfo = entity.getFavoritesDetail();
        this.forbiddenPlayerIds = entity.getForbiddenPlayer();
        this.sendSysMailId = entity.getSendSysMailId();
    }

    private Map<Integer, PlayerIcon> playerIconMap = new HashMap<>();
    private PlayerFavoritesInfo playerFavoritesInfo = new PlayerFavoritesInfo();

    public void init() {
        this.picture = new byte[0];
        this.powerRefreshTime = System.currentTimeMillis();
        this.energyRefreshTime = System.currentTimeMillis();
        this.pictureRefreshTime = System.currentTimeMillis();
        PlayerProfileConfig playerProfileConfig = PlayerProfileConfig.getInstance();
        for (PlayerProfile playerProfile : playerProfileConfig.getSpecList()) {
            if (playerProfile.getUnlockType() == PlayerImageUnlockType.UNLOCK.getType()) {
                if (PlayerImageTypeEnum.ICON.getType() == playerProfile.getType()) {
                    PlayerIcon playerIcon = new PlayerIcon();
                    playerIcon.setIconId(playerProfile.getId());
                    playerIcon.setTime(-1L);
                    this.playerIconMap.put(playerIcon.getIconId(), playerIcon);
                    if (playerProfile.getInitWear() == 1)
                        this.iconId = playerProfile.getId();
                    continue;
                }
                PlayerFrame playerFrame = new PlayerFrame();
                playerFrame.setFrameId(playerProfile.getId());
                playerFrame.setTime(-1L);
                this.playerFrameMap.put(playerFrame.getFrameId(), playerFrame);
                if (playerProfile.getInitWear() == 1)
                    this.frameId = playerProfile.getId();
            }
        }
        this.playerFavoritesInfo.setFavoritesSize(GameParameters.bookMark_Default.getInt());
    }

    public void update(PlayerCommonEntity entity) {
        entity.setLevel(this.level);
        entity.setExp(this.exp);
        entity.setProfileIconId(this.iconId);
        entity.setProfileFrameId(this.frameId);
        entity.setProfilePicture(this.picture);
        entity.setPowerRefreshTime(this.powerRefreshTime);
        entity.setEnergyRefreshTime(this.energyRefreshTime);
        entity.setPictureRefreshTime(this.pictureRefreshTime);
        entity.setFrameDetail(new ArrayList<>(this.playerFrameMap.values()));
        entity.setIconDetail(new ArrayList<>(this.playerIconMap.values()));
        entity.setStatisticsDetail(this.playerStatistics.getVarMap());
        entity.setFavoritesDetail(this.playerFavoritesInfo);
        entity.setForbiddenPlayer(this.forbiddenPlayerIds);
        entity.setSendSysMailId(this.sendSysMailId);
    }
}
