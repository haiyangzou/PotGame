package org.pot.room.kcp;

import io.netty.channel.DefaultEventLoop;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 内部定时器
 * Created by JinMiao
 * 2020/11/24.
 */
public class TimerThreadPool {
    /**定时线程池**/
    //private static final ScheduledThreadPoolExecutor scheduled  = new ScheduledThreadPoolExecutor(1,new TimerThreadFactory());
    private static final DefaultEventLoop EVENT_EXECUTORS = new DefaultEventLoop();



    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long milliseconds){
        return EVENT_EXECUTORS.scheduleWithFixedDelay(command,milliseconds,milliseconds, TimeUnit.MILLISECONDS);
    }


    public void stop()
    {
        if(!EVENT_EXECUTORS.isShuttingDown()){
            EVENT_EXECUTORS.shutdownGracefully();
        }
    }
}
