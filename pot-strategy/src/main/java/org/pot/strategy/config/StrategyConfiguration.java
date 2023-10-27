package org.pot.strategy.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StrategyConfiguration {
    @Value("${global.server.host}")
    private String globalServerHost;
    @Value("${global.server.http.port}")
    private String globalServerHttpPort;
    @Value("${global.client.resource.url}")
    private String clientResourceUrl = StringUtils.EMPTY;
    @Value("${global.server.gateway.host}")
    private String clientGatewayHost = StringUtils.EMPTY;
    @Value("${global.server.gateway.port}")
    private int clientGatewayPort = 0;

}
