package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;

@Getter
public class GlobalServerConfig extends EndPointConfig {
    protected int httpPort;
    protected String userLoginUrl = StringUtils.EMPTY;
    protected String userReconnectUrl = StringUtils.EMPTY;
    protected String httpUrlPrefix = StringUtils.EMPTY;
    protected String gameServerListUrl = StringUtils.EMPTY;
    protected String serverInfoUrl = StringUtils.EMPTY;

    public static GlobalServerConfig loadGlobalServerConfig(Configuration config) {
        String host = config.getString("global.server.host", null);
        int httpPort = config.getInt("global.server.http.port", 80);
        if (StringUtils.isBlank(host)) return null;
        return new GlobalServerConfig(host, httpPort);
    }

    public GlobalServerConfig(String host, int httpPort) {
        this.host = host;
        this.httpPort = httpPort;
        this.initialize();
    }

    private void initialize() {
        this.httpUrlPrefix = "http://" + host + ":" + httpPort;
        this.userLoginUrl = httpUrlPrefix + "/user/login";
        this.userReconnectUrl = httpUrlPrefix + "/user/reconnect";
        this.gameServerListUrl = httpUrlPrefix + "/gameServer/getList";

    }
}
