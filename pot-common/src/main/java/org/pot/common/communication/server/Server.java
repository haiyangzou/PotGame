package org.pot.common.communication.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class Server implements Comparable<Server> {
    @JsonProperty("type")
    private Integer typeId;
    @JsonProperty("serverId")
    private Integer serverId;
    private String serverName;
    private String typeName;
    @JsonProperty("host")
    private String host;
    @JsonProperty("port")
    private Integer port;
    private Integer httpPort;
    private Integer rpcPort;
    @JsonProperty("openTime")
    private Date openTime;
    @JsonProperty("target")
    private Integer targetServerId;
    private String remark;
    private Date createTime;
    private Date updateTime;

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

    public Server(int typeId, int serverId, String host, int port, int httpPort, int rpcPort) {
        this.typeId = typeId;
        this.serverId = serverId;
        this.host = host;
        this.port = port;
        this.correct(httpPort, rpcPort);
    }

    public void correct(int httpPort, int rpcPort) {
        this.typeName = ServerType.valueOf(typeId).name();
        this.serverName = typeName + "[" + serverId + "]";
        this.httpPort = httpPort;
        this.rpcPort = rpcPort;
        if (this.openTime == null) {
            this.openTime = new Date();
        }
        if (this.targetServerId == null) {
            this.targetServerId = 0;
        }
        this.remark = StringUtils.EMPTY;
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
