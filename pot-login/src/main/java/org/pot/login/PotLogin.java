package org.pot.login;

import org.pot.common.PotPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ResourceUtils;

/**
 * 处理登录，支付等接口,无状态服务器
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})

@ComponentScan(basePackageClasses = PotPackage.class)
@PropertySource(value = ResourceUtils.FILE_URL_PREFIX + "./conf/global-application.properties")
public class PotLogin {
    public static void main(String[] args) {
        SpringApplication.run(PotLogin.class, args);
    }
}
