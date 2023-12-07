package org.pot.game.engine.player.common;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.player.module.common.PlayerStatistics;

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
    private PlayerStatistics playerStatistics = new PlayerStatistics();

    public PlayerCommonInfo() {
    }
}
