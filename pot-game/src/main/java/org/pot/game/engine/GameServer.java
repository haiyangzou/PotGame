package org.pot.game.engine;

import lombok.Data;

import java.util.Date;

@Data
public class GameServer {
    private Date openTime;

    public long getOpenTimestamp() {
        return openTime == null ? 0 : openTime.getTime();
    }
}
