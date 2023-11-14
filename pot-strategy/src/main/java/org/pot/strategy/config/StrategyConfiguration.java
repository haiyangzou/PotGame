package org.pot.strategy.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.config.GlobalServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StrategyConfiguration {
    @Value("${global.server.host}")
    private String globalServerHost;
    @Value("${global.server.http.port}")
    private String globalServerHttpPort;
    @Value("${strategy.client.resource.url}")
    private String clientResourceUrl = StringUtils.EMPTY;
    @Value("${strategy.client.gateway.host}")
    private String clientGatewayHost = StringUtils.EMPTY;
    @Value("${strategy.client.gateway.port}")
    private int clientGatewayPort = 0;

    @Bean
    public GlobalServerConfig globalServerConfig(@Value("${global.server.host}") String host,
                                                 @Value("${global.server.http.port:80}") int httpPort) {
        return new GlobalServerConfig(host, httpPort);
    }

}
