package org.pot.login.component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.login.beans.ServerConst;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WorkOps {
    @Resource
    private ServerConst serverConst;
    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    private void init() {
        threadPoolExecutor = new ThreadPoolExecutor(
                serverConst.getWorkerThreadMin(),
                serverConst.getWorkerThreadMax(),
                serverConst.getWorkerKeepAliveSeconds(), TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(serverConst.getWorkerQueueMaxSize()),
                new ThreadFactoryBuilder().setNameFormat("Worker-%d").build());
    }

    public void execute(Runnable command) {
        threadPoolExecutor.execute(command);
    }

    public void destroy() {
        ExecutorUtil.shutdownExecutor(threadPoolExecutor);
    }
}
