package org.pot.common.communication.server;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@Data
public class Server implements Comparable<Server> {
    private Integer typeId;
    private Integer serverId;
    private String serverName;
    private String typeName;
    private String host;
    private int port;
    @JsonProperty("openTime")
    private Date openTime;
    private int httpPort;
    private int rpcPort;
    private int targetServerId;

    public ServerId getServerIdObject() {
        return ServerId.of(typeId, serverId);
    }

    @Override
    public int compareTo(Server o) {
        return 0;
    }

    public long getOpenTimestamp() {
        return openTime == null ? 0 : openTime.getTime();
    }

    public Server(int typeId, Integer serverId, String serverName, String host, int port, int httpPort, int rpcPort, Date openTime, int targetServerId) {
        this.typeId = typeId;
        this.serverId = serverId;
        this.serverName = serverName;
        this.typeName = ServerType.valueOf(typeId).name();
        this.host = host;
        this.port = port;
        this.openTime = openTime;
        this.targetServerId = targetServerId;
        this.httpPort = httpPort;
        this.rpcPort = rpcPort;
    }
}
