package org.pot.room;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.pot")
@ImportResource(locations = {"classpath:config/room.xml"})
@PropertySource(value = {"classpath:application-${spring.profiles.active}.properties"}, encoding = "UTF-8")
@ConfigurationPropertiesScan
public class AppConfig {

}
