package org.pot.login.config;

import lombok.extern.slf4j.Slf4j;
import org.pot.login.beans.ServerConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ServerConstConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "server.const")
    public ServerConst serverConst() {
        return new ServerConst();
    }
}
