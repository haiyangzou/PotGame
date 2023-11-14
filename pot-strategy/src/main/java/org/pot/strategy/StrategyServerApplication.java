package org.pot.strategy;

import org.pot.PotPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ResourceUtils;

/**
 * 处理登录，支付等接口,无状态服务器
 */
@EnableScheduling
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})

@ComponentScan(basePackageClasses = PotPackage.class)
@PropertySource(value = ResourceUtils.FILE_URL_PREFIX + "./conf/strategy-application.properties")
public class StrategyServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StrategyServerApplication.class, args);
    }
}
