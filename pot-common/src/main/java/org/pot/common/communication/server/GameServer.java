package org.pot.common.communication.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class GameServer implements Comparable<GameServer>, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer serverId;
    private String serverName;
    private String host;
    private Integer port;
    private Integer httpPort;
    private Integer rpcPort;
    private Date openTime;
    private Integer targetServerId;
    private Integer unionServerId;
    private String gameVersion;
    private Date maintainEndTime;
    private Integer totalMaxCount;
    private Integer dayMaxCount;
    private Integer hourMaxCount;
    private Long maintainNotice;
    private Date createTime;
    private Date updateTime;

    public GameServer() {
    }

    @Override
    public int compareTo(GameServer o) {
        return 0;
    }

    public GameServer(Server server, int unionServerId) {
        if (server.getTypeId() != ServerType.GAME_SERVER.getId()) {
            throw new IllegalArgumentException();
        }
        this.serverId = server.getServerId();
        this.serverName = server.getServerName();
        this.host = server.getHost();
        this.port = server.getPort();
        this.unionServerId = unionServerId;
    }

    public Server toServer() {
        return new Server(ServerType.GAME_SERVER.getId(), serverId, serverName, host, port, httpPort, rpcPort, getOpenTime(), targetServerId);
    }

    public long getOpenTimeStamp() {
        return openTime == null ? 0 : openTime.getTime();
    }

    public long getMaintainEndTimestamp() {
        return maintainEndTime == null ? 0 : maintainEndTime.getTime();
    }

    public int getServerStatus() {
        final long now = System.currentTimeMillis();
        final long openTimeStamp = getOpenTimeStamp();
        final long maintainEndTimestamp = getMaintainEndTimestamp();
        if (now < openTimeStamp) {
            return 0;
        } else if (now < maintainEndTimestamp) {
            return 2;
        } else {
            return 1;
        }
    }
}
