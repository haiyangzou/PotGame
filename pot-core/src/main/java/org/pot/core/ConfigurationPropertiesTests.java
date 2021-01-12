package org.pot.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pot.core.netty.config.NettyProperties;
import org.pot.core.netty.config.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NettyProperties.class, ServerProperties.class})
@ConfigurationPropertiesScan
@PropertySource(value = {"classpath:config/pot.properties"}, encoding = "UTF-8")
public class ConfigurationPropertiesTests {

  @Autowired
  ServerProperties serverProperties;
  @Autowired
  NettyProperties nettyProperties;

  @Test
  public void contextLoads() {
    System.out.println(serverProperties);
    System.out.println(nettyProperties);
  }
}
