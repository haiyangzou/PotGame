package org.pot.common.communication.server;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
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
}
