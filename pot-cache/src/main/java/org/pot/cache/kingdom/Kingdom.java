package org.pot.cache.kingdom;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class Kingdom {
    @Getter
    private int serverId;
    @Getter
    private volatile String battleId = StringUtils.EMPTY;
    @Getter
    private volatile String kingPlayerId;
    @Getter
    private volatile String kingdomName;
    @Getter
    private volatile String kingdomFlag;
    @Getter
    private volatile String kingdomNotice;
    @Getter
    private volatile String kingdomRenameTime;

    public static String buildRedisKey(int sid) {
        return "GAME_SERVER_KINGDOM_INFO_" + StringUtils.leftPad(String.valueOf(sid), 4, '0');
    }
}

