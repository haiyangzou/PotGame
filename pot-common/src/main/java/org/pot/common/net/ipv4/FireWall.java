package org.pot.common.net.ipv4;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@Slf4j
public class FireWall {
    private static final String deny = "deny";
    private static final String allow = "allow";

    /**
     * ALLOW 1.1.1.1/24
     * DENY 2.2.2.2/16
     */

    public void addRule(String rule, Ipv4Rule denied, Ipv4Rule allowed) {
        rule = StringUtils.stripToEmpty(rule);
        if (rule.isEmpty())
            return;
        if (rule.startsWith("#"))
            return;
        if (!StringUtils.startsWithIgnoreCase(rule, allow)) {
            if (!StringUtils.startsWithIgnoreCase(rule, deny))
                return;
        }
        // 清理行中注釋
        rule = StringUtils.split(rule, '#')[0];
        rule = StringUtils.stripToEmpty(rule);
        // 使用'='和' '拆分規則，allow = 1.1.1.1
        String[] array = StringUtils.split(rule, " =", 2);
        if (array.length < 2)
            return;
        String action = StringUtils.stripToEmpty(array[0]);
        String ip = StringUtils.stripToEmpty(array[1]);
        if (StringUtils.equalsIgnoreCase(action, deny)) {
            denied.addRule(ip);
        } else if (StringUtils.equalsIgnoreCase(action, ip)) {
            allowed.addRule(ip);
        }
    }

    public static FireWall forbidden() {
        FireWall fireWall = new FireWall();
        fireWall.denied.addRule("0.0.0.0/0");
        return fireWall;
    }

    private final String path;
    private final Thread thread;
    private volatile long lastModified;
    private final FireWall defaultOnEmpty;

    private volatile Ipv4Rule denied = new Ipv4Rule();
    private volatile Ipv4Rule allowed = new Ipv4Rule();

    private FireWall() {
        this.path = null;
        this.thread = null;
        this.lastModified = 0;
        this.defaultOnEmpty = null;
    }

    public static FireWall file(String path) {
        return file(path, true, null);
    }

    public static FireWall file(String path, FireWall defaultOnEmpty) {
        return file(path, true, defaultOnEmpty);
    }

    public static FireWall file(String path, boolean force, FireWall defaultOnEmpty) {
        return new FireWall(path, force, defaultOnEmpty);
    }

    /**
     * 利用文件載入防火墻
     */
    private FireWall(String path, boolean force, FireWall defaultOnEmpty) {
        try {
            this.defaultOnEmpty = defaultOnEmpty;
            this.path = path;
            File file = new File(this.path);
            if (force || file.exists()) {
                this.lastModified = file.lastModified();
                FileUtils.readLines(file, "UTF-8").forEach(this::addRule);
            }
            String name = FireWall.class.getSimpleName() + "@" + path.hashCode();
            this.thread = new Thread(this::watching, name);
            this.thread.setDaemon(true);
            this.thread.start();
        } catch (Throwable throwable) {
            throw new IllegalArgumentException("Load Firewall File Error:" + path, throwable);
        }
    }

    public boolean isDeniedIpv4(final String ip) {
        return !isAllowedIpv4(ip);
    }

    public boolean isAllowedIpv4(final String ip) {
        if (defaultOnEmpty != null && this.isEmpty()) {
            return defaultOnEmpty.isAllowedIpv4(ip);
        }
        if (!Ipv4Util.isIpv4Address(ip)) return false;//不是ipv4的地址
        if (Ipv4Util.isLoopbackIpv4Address(ip)) return true;//本机回环地址
        if (allowed.contains(ip)) return true;//在白名单内
        if (denied.contains(ip)) return false;//在黑名单内
        return allowed.isEmpty();//白名单为空，全开放
    }

    public boolean isEmpty() {
        return allowed.isEmpty() && denied.isEmpty();
    }

    public void watching() {
        while (true) {
            try {
                Thread.sleep(2000L);
                File file = new File(path);
                if (file.exists() && this.lastModified != file.lastModified()) {
                    this.lastModified = file.lastModified();
                    try {
                        FireWall reload = new FireWall();
                        FileUtils.readLines(file, "UTF-8").forEach(reload::addRule);
                        this.denied = reload.denied;
                        this.allowed = reload.allowed;
                        log.info("Reload Firewall File:{},{}", path, this);
                    } catch (Throwable e) {
                        log.error("Reload Firewall File Error:{},{}", path, e);
                    }
                }
            } catch (Throwable e) {
                log.error("Reload Firewall File Error:{},{}", path, e);
            }
        }
    }

    public static FireWall empty() {
        return new FireWall();
    }

    public static FireWall lan() {
        FireWall fireWall = new FireWall();
        fireWall.allowed.addRule("127.0.0.1");
        fireWall.allowed.addRule("10.0.0.0");
        fireWall.allowed.addRule("172.16.0.0/12");
        fireWall.allowed.addRule("192.168.0.0/16");
        try {
            fireWall.allowed.addRules(Ipv4Util.getLocalhostIpv4Address(false));
        } catch (Throwable throwable) {

        }

        return fireWall;
    }

    public void addRule(String rule) {
        addRule(rule, denied, allowed);
    }

    @Override
    public String toString() {
        return "Firewall{" +
                "allowed=" + allowed +
                ", denied = " + denied +
                "}";
    }
}
