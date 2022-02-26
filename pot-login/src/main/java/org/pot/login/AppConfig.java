package org.pot.login;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.pot")
@ImportResource(locations = {"classpath:config/login.xml"})
@PropertySource(value = {"classpath:application.properties"}, encoding = "UTF-8")
@ConfigurationPropertiesScan
public class AppConfig {

}
