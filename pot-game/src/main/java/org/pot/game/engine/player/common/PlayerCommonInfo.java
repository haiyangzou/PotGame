package org.pot.game.engine.player.common;

import lombok.Getter;
import lombok.Setter;

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

    public PlayerCommonInfo() {
    }
}
