package org.pot.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏全局上下文
 *
 * @author zouhaiyang
 */
public class ServerContext {

    private static final String APPLICATION_CONTEXT = "_a_";
    private static final String ADMIN_COMMEND_HANDLER = "_admin_cmd_";
    private static final String CONFIG_MANAGER = "_config_manager";
    private static Map<String, Object> map = new HashMap<String, Object>();

    public static AnnotationConfigApplicationContext getApplicationContext() {
        return (AnnotationConfigApplicationContext) map.get(APPLICATION_CONTEXT);
    }

    public static void setApplicationContext(AnnotationConfigApplicationContext context) {
        map.put(APPLICATION_CONTEXT, context);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Class<?>> getAdminCommendHandler() {
        Map<String, Class<?>> adminHandlerMap = (Map<String, Class<?>>) map
            .get(ADMIN_COMMEND_HANDLER);
        if (adminHandlerMap == null) {
            adminHandlerMap = new HashMap<String, Class<?>>();
            map.put(ADMIN_COMMEND_HANDLER, adminHandlerMap);
        }
        return adminHandlerMap;
    }

    public static void set(String key, Object value) {
        map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) map.get(key);
    }
}
