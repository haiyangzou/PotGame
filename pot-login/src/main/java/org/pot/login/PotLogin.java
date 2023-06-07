package org.pot.login;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.PooledExecutionServiceConfigurationBuilder;
import org.pot.core.ServerContext;
import org.pot.login.service.ShutdownThread;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理登录，支付等接口,无状态服务器
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})

@ComponentScan(value ={"org.pot"})
@EnableMongoRepositories
public class PotLogin implements CommandLineRunner {

    private static ExecutorService es = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ServerContext.setApplicationContext(context);
        context.register(AppConfig.class);
        context.refresh();
        Runtime.getRuntime().addShutdownHook(context.getBean(ShutdownThread.class));
    }

    @Override
    public void run(String... args) throws Exception {

    }
    @Bean
    public CacheManager ehCacheManager() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .using(PooledExecutionServiceConfigurationBuilder
                        .newPooledExecutionServiceConfigurationBuilder()
                        .defaultPool("defaultPool", 1, 3)
                        .pool("writeBehindPool", 1, 3)
                        .build())
                .build(true);
        return cacheManager;
    }
}
