package org.pot.game.engine.player.common;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.player.module.common.PlayerStatistics;

import java.util.HashSet;
import java.util.Set;

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
    private Set<Long> forbiddenPlayerIds = new HashSet<>();

    public PlayerCommonInfo() {
    }
}
