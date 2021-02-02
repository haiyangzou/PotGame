package org.pot.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 处理登录，支付等接口,无状态服务器
 */
@EnableMongoRepositories
@Slf4j
public class PotApp implements CommandLineRunner {

    private static ExecutorService es = Executors.newFixedThreadPool(1);
    public static void main(String[] args) {
        try {
            People people = new Teacher();
            InvocationHandler handler = new WorkHandler(people);
            People proxy = (People) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                people.getClass().getInterfaces(), handler);
            proxy.work();
            proxy.study();
//            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//            ServerContext.setApplicationContext(context);
//            context.register(AppConfig.class);
//            context.refresh();
//            Runtime.getRuntime().addShutdownHook(context.getBean(ShutdownThread.class));
        } catch (Exception e) {
            log.error("Server Start Fail.", e);
            //ApplicationUtil.deletePidFile(new File(SharedConstant.PID_FILE_PATH));
            System.exit(-1);
        }
    }

    @Override
    public void run(String... args) throws Exception {

    }

    interface People {

        public String work();

        public String study();
    }

    static class Teacher implements People {

        @Override
        public String work() {
            System.out.println("老师教书育人..." + a());
            return "教书";
        }

        private String a() {
            return "aaaaa";
        }

        @Override
        public String study() {
            System.out.println("老师也学习");
            return "学习";
        }
    }

    static class LogicMethod implements Runnable {

        private Method method;
        private Object obj;
        private Object[] args;

        public LogicMethod(Method method, Object obj, Object[] args) {
            this.method = method;
            this.obj = obj;
            this.args = args;
        }

        public void invoke() throws Exception {
            method.invoke(obj, args);
        }

        @Override
        public void run() {
            try {
                this.invoke();
            } catch (Exception e) {
                log.error("LogicMethod invoke error:" + e.getMessage(), e);
            }
        }
    }

    static class WorkHandler implements InvocationHandler {

        //代理类中的真实对象
        private Object obj;

        public WorkHandler() {
            // TODO Auto-generated constructor stub
        }

        //构造函数，给我们的真实对象赋值
        public WorkHandler(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //在真实的对象执行之前我们可以添加自己的操作
            System.out.println("before invoke。。。");
            es.execute(new LogicMethod(method, obj, args));
            //在真实的对象执行之后我们可以添加自己的操作
            System.out.println("after invoke。。。");
            return null;
        }
    }
}
