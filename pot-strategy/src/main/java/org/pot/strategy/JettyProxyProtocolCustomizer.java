package org.pot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ProxyConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JettyProxyProtocolCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory>, Ordered {
    @Override
    public void customize(ConfigurableJettyWebServerFactory factory) {
        factory.addServerCustomizers(server -> {
            for (Connector connector : server.getConnectors()) {
                if (connector instanceof ServerConnector) {
                    ServerConnector serverConnector = (ServerConnector) connector;
                    serverConnector.addFirstConnectionFactory(new ProxyConnectionFactory());
                }
            }
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
