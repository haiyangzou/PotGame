package org.pot.login.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;
import org.pot.common.net.ipv4.FireWall;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class FireWallConfiguration {
    @Bean
    public FireWall webFirewall(@Value("${web.firewall.file:}") String webFirewallFile) {
        FireWall webFirewall;
        if (StringUtils.isNotBlank(webFirewallFile)) {
            webFirewall = FireWall.file(Constants.Env.getContextPath() + webFirewallFile);
        } else {
            webFirewall = FireWall.empty();
        }
        return webFirewall;
    }

    @Bean
    public FireWall loginFirewall(@Value("${login.firewall.file:}") String loginFirewallFile) {
        FireWall loginFirewall;
        if (StringUtils.isNotBlank(loginFirewallFile)) {
            loginFirewall = FireWall.file(Constants.Env.getContextPath() + loginFirewallFile, FireWall.forbidden());
        } else {
            loginFirewall = FireWall.empty();
        }
        return loginFirewall;
    }
}
