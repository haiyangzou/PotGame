package org.pot.core.engine;

import com.google.common.collect.Lists;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.relect.FieldUtil;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerFactory<K, T> {
    private final Map<K, FastClass> handlerClassMap = new ConcurrentHashMap<>();
    private final Map<K, T> handlerCache = new ConcurrentHashMap<>();
    private final boolean cacheEnabled;
    private final T defaultHandler;

    public HandlerFactory() {
        this(true, null);
    }

    public HandlerFactory(boolean cacheEnable, T defaultHandler) {
        this.cacheEnabled = cacheEnable;
        this.defaultHandler = defaultHandler;
    }

    /**
     * addHandler(K key,Class<? extends T> handlerClass)
     * 
     */

    public void addHandler(K key, Class<? extends T> handlerClass) {
        try {
            FastClass fastClass = FastClass.create(handlerClass);
            T handler = (T) fastClass.newInstance();
            if (cacheEnabled) {
                // 如果使用缓存时，不能存在可变的成员变量，存在可变的成员变量的则不能被缓存，但是仍然可以使用FastClass方式
                List<Field> fields = Lists.newArrayList(FieldUtil.getAllFieldsList(handlerClass));
                fields.removeIf(field -> Modifier.isFinal(field.getModifiers()));
                if (fields.isEmpty()) {
                    handlerCache.put(key, handler);
                } else {
                    // throw new ServerException("enable handler cache fail,key=" + key + ", handler
                    // = "
                    // + ClassUtil.getAbbreviatedName(handlerClass));
                }
                handlerClassMap.put(key, fastClass);
            }
            handlerClassMap.put(key, fastClass);
        } catch (Exception e) {
            // throw new ServerException("enable handler cache fail,key=" + key + ", handler
            // = "
            // + ClassUtil.getAbbreviatedName(handlerClass), e);
        }

    }

    public T getHandler(K key) {
        T handler;
        FastClass cls = handlerClassMap.get(key);
        if (cls == null) {
            return defaultHandler;
        }
        try {
            handler = (T) cls.newInstance();
            if (cacheEnabled) {
                handlerCache.putIfAbsent(key, handler);
            }
        } catch (InvocationTargetException e) {
            throw new ServiceException("create handler occur an error. key = " + key + ",handler=" + cls.getName());
        }
        return handler;
    }
}
