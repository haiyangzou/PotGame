package org.pot.common.communication.server;

public class ServerId {
    public final ServerType serverType;
    public final Integer id;

    public ServerId(ServerType serverType, Integer id) {
        this.serverType = serverType;
        this.id = id;
    }

    public static ServerId of(int serverType, int serverId) {
        return of(ServerType.valueOf(serverType), serverId);
    }

    public static ServerId of(ServerType serverType, int serverId) {
        return new ServerId(serverType, serverId);
    }

}
