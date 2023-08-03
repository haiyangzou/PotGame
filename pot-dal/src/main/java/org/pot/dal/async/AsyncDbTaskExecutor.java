package org.pot.dal.async;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.util.Indicator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class AsyncDbTaskExecutor {
    private final Object mutex = new Object();
    private volatile boolean shutdown = true;
    private final Processor[] processors;
    private final AtomicLong totalSubmitCount = new AtomicLong(0);
    private final AtomicLong totalExecuteCount = new AtomicLong(0);
    private final AtomicLong totalExecuteNanos = new AtomicLong(0);
    private final Indicator indicator = Indicator.builder("tps").build();

    public AsyncDbTaskExecutor(int processorCount) {
        this.processors = new Processor[processorCount];
        this.start();
    }

    public void start() {
        synchronized (mutex) {
            if (shutdown) {
                shutdown = false;
                for (int i = 0; i < processors.length; i++) {
                    if (processors[i] == null || processors[i].isClosed() || !processors[i].isAlive()) {
                        processors[i] = new Processor();
                        processors[i].setDaemon(false);
                        processors[i].setName(this.getClass().getSimpleName() + "-" + i);
                        processors[i].start();
                    }
                }
            }
        }
    }

    public void stop() {
        synchronized (mutex) {
            shutdown = true;
        }
    }

    public boolean isClosed() {
        return shutdown && Arrays.stream(processors).allMatch(Processor::isClosed);
    }

    public long getCurrentTaskCount() {
        long result = 0;
        for (Processor processor : processors) {
            if (processor != null) {
                result += processor.taskQueue.size();
            }
        }
        return result;
    }

    public Map<String, Object> getStatus() {
        String name = this.getClass().getSimpleName();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(name + "_Current_Task_Count", getCurrentTaskCount());
        map.put(name + "_Total_Submit_Count", totalSubmitCount.get());
        map.put(name + "_Total_Execute_Count", totalExecuteCount.get());
        map.put(name + "_Total_Execute_Time", DateTimeUtil.computeNanosToMillis(totalExecuteNanos.get()) + "ms");
        return map;
    }

    public void submit(final IAsyncDbTask asyncDbTask) {
        if (shutdown) {
            throw new IllegalStateException("closed state cannot submit task");
        }
        long taskId = Math.max(0, asyncDbTask.getId());
        int index = (int) (taskId % processors.length);
        processors[index].taskQueue.offer(asyncDbTask);
        totalSubmitCount.incrementAndGet();
    }

    private final class Processor extends Thread {
        @Getter
        private volatile boolean closed;
        private final BlockingQueue<IAsyncDbTask> taskQueue = new LinkedBlockingDeque<>();

        @Override
        public void run() {
            while (true) {
                synchronized (mutex) {
                    if (shutdown && taskQueue.isEmpty()) {
                        closed = true;
                        break;
                    }
                }
                while (!shutdown || !taskQueue.isEmpty()) {
                    try {
                        process();
                    } catch (Throwable throwable) {
                        log.info("AsyncDbTaskExecutor Processor Error", throwable);
                    }
                }
            }
        }

        private void process() throws InterruptedException {
            IAsyncDbTask asyncDbTask = taskQueue.poll(10, TimeUnit.MILLISECONDS);
            if (asyncDbTask != null) {
                long nano = System.nanoTime();
                asyncDbTask.execute();
                indicator.increment();
                totalExecuteCount.incrementAndGet();
                long elapsed = System.nanoTime() - nano;
                if (elapsed > 0) {
                    totalExecuteNanos.updateAndGet(operand -> elapsed + operand);
                }
            }
        }
    }
}
