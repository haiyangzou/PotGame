package org.pot.web.diagnose;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.pot.common.util.NumberUtil;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JvmMonitor {
    @Getter
    private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    @Getter
    private static final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    @Getter
    private static final ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    @Getter
    private static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    @Getter
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    @Getter
    private static final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
    @Getter
    private static final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

    /**
     * 获取系统负载
     */
    public static double getSystemLoad() {
        return operatingSystemMXBean.getSystemLoadAverage();
    }

    /**
     * 获取CPU个数
     */
    public static int getAvailableProcessors() {
        return operatingSystemMXBean.getAvailableProcessors();
    }

    public static Map<String, String> getFileDesc() {
        Map<String, String> status = Maps.newLinkedHashMapWithExpectedSize(2);
        String[] attributeNames = new String[]{"MaxFileDescriptorCount", "OpenFileDescriptorCount"};
        try {
            ObjectName name = new ObjectName("java.lang:type=OperatingSystem");
            AttributeList attributeList = getPlatformMBeanServer().getAttributes(name, attributeNames);
            for (Object o : attributeList) {
                Attribute attribute = (Attribute) o;
                status.put(attribute.getName(), attribute.getValue().toString());
            }
        } catch (Throwable throwable) {
            log.error("getFileDesc error", throwable);
        }
        return status;
    }

    /**
     * 获取所有线程数
     */
    public static int getAllThreadsCount() {
        return threadBean.getThreadCount();
    }

    /**
     * 获取峰值线程数
     */
    public static int getPeakThreadCount() {
        return threadBean.getPeakThreadCount();
    }

    /**
     * 获取live daemon线程数
     */
    public static int getDaemonThreadCount() {
        return threadBean.getDaemonThreadCount();
    }

    /**
     * 获取启动以来创建的线程数
     */
    public static long getTotalStartedThreadCount() {
        return threadBean.getTotalStartedThreadCount();
    }

    public static int getDeadLockCount() {
        long[] deadLockIds = threadBean.findDeadlockedThreads();
        return ArrayUtils.getLength(deadLockIds);
    }

    public static ThreadInfo[] dumpThreadStack() {
        return threadBean.dumpAllThreads(true, true);
    }

    public static String getHeapMemoryUsage() {
        return formatMemoryUsage(memoryMXBean.getHeapMemoryUsage());
    }

    public static String getNonHeapMemoryUsage() {
        return formatMemoryUsage(memoryMXBean.getNonHeapMemoryUsage());
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static Map<String, String> getGCStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            long count = garbageCollectorMXBean.getCollectionCount();
            long time = garbageCollectorMXBean.getCollectionTime();
            if (count > 0) {
                status.put(garbageCollectorMXBean.getName(), String.format("Count:%d Time:%d,Avg:%.2f", count, time, ((double) time / count)));
            }
        }
        return status;
    }

    public static Map<String, String> getMemoryPoolUsage() {
        Map<String, String> status = new LinkedHashMap<>();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            status.put(memoryPoolMXBean.getName() + "Usage", formatMemoryUsage(memoryPoolMXBean.getUsage()));
            status.put(memoryPoolMXBean.getName() + "PeakUsage", formatMemoryUsage(memoryPoolMXBean.getPeakUsage()));
            if (memoryPoolMXBean.getCollectionUsage() != null) {
                status.put(memoryPoolMXBean.getName() + "CollectionUsage", formatMemoryUsage(memoryPoolMXBean.getCollectionUsage()));
            }
        }
        return status;
    }

    public static Map<String, String> getJVMStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("Processors", String.valueOf(getAvailableProcessors()));
        status.put("SystemLoad", String.format("%.2f", getSystemLoad()));
        status.put("DeadLockCount", String.valueOf(getDeadLockCount()));
        status.put("AllThreadCount", String.valueOf(getPeakThreadCount()));
        status.put("DaemonThreadsCount", String.valueOf(getDaemonThreadCount()));
        status.put("TotalStarted", String.valueOf(getTotalStartedThreadCount()));
        status.putAll(getFileDesc());
        status.put("HeapMemoryUsage", getHeapMemoryUsage());
        status.put("NonHeapMemoryUsage", getNonHeapMemoryUsage());
        status.putAll(getGCStatus());
        status.putAll(getMemoryPoolUsage());
        return status;
    }

    public static String formatMemoryUsage(MemoryUsage memoryUsage) {
        return memoryUsage == null ? null : String.format("init:%s,used:%s,committed:%s,max:%s",
                NumberUtil.byteCountToDisplaySize(memoryUsage.getInit()),
                NumberUtil.byteCountToDisplaySize(memoryUsage.getUsed()),
                NumberUtil.byteCountToDisplaySize(memoryUsage.getCommitted()),
                NumberUtil.byteCountToDisplaySize(memoryUsage.getMax()));
    }


}
