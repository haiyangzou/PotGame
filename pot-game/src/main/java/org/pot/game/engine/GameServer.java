package org.pot.game.engine;

import lombok.Data;

import java.util.Date;

@Data
public class GameServer {
    private Date openTime;
    private Integer serverId;
    private String serverName;
    private String host;
    private Integer port;
    private Integer httpPort;
    private Integer rpcPort;
    private Integer targetServerId;
    private Integer unionServerId;
    private String gameVersion;
    private Date maintainEndTime;
    private Long maintainNotice;
    private Integer totalMaxCount;
    private Integer dayMaxCount;
    private Integer hourMaxCount;
    private Date createTime;
    private Date updateTime;


    public long getOpenTimestamp() {
        return openTime == null ? 0 : openTime.getTime();
    }
}
