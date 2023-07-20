package org.pot.common.communication.server;

import lombok.Data;

@Data
public class Server implements Comparable<Server> {
    private Integer typeId;
    private Integer serverId;
    private String serverName;
    private String typeName;
    private String host;
    private int port;

    public ServerId getServerIdObject() {
        return ServerId.of(typeId, serverId);
    }

    @Override
    public int compareTo(Server o) {
        return 0;
    }
}
