package org.pot.game;

import lombok.extern.slf4j.Slf4j;
import org.pot.core.ServerContext;
import org.pot.game.service.ShutdownThread;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理登录，支付等接口,无状态服务器
 */
@EnableMongoRepositories
@Slf4j
public class PotGame implements CommandLineRunner {

    private static ExecutorService es = Executors.newFixedThreadPool(1);
    public static void main(String[] args) {
        try {
            
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            ServerContext.setApplicationContext(context);
            context.register(AppConfig.class);
            context.refresh();
            Runtime.getRuntime().addShutdownHook(context.getBean(ShutdownThread.class));
        } catch (Exception e) {
            log.error("Server Start Fail.", e);
            //ApplicationUtil.deletePidFile(new File(SharedConstant.PID_FILE_PATH));
            System.exit(-1);
        }
    }
    @Override
    public void run(String... args) throws Exception {
    }
}
