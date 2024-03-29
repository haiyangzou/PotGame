//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.pot.common.util;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.util.file.TextFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        //支持序列化guava的不可变集合
        MAPPER.registerModule(new GuavaModule());
        //支持序列化时间,JSR-310
        MAPPER.registerModule(new JavaTimeModule());
        //对Map形式存储的进行排序
        MAPPER.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        //去掉默认的时间格式
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //单引号处理
        MAPPER.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        //反序列化时,属性不存在的兼容处理
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //空值不序列化
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置格式化
        MAPPER.configOverride(java.util.Date.class).setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss"));
        MAPPER.configOverride(java.time.LocalDate.class).setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd"));
        MAPPER.configOverride(java.time.LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss"));
        //设置只能关注Field
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        //没有属性的类也需要序列化
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public JsonUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T value) {
        return (T) parseJson(toJson(value), value.getClass());
    }

    public static void registerModules(Module... modules) {
        if (ArrayUtils.isNotEmpty(modules)) {
            MAPPER.registerModules(modules);
        }
    }

    public static boolean checkSubtypes(Class<?> cls) {
        if (!cls.isAnnotationPresent(JsonTypeName.class)) {
            throw new IllegalArgumentException("Only Allow Use JsonTypeName");
        }
        if (!ConstructorUtil.containsNoneParamConstructor(cls)) {
            throw new IllegalArgumentException(String.format("can't found none param constructor in %s", ClassUtil.getAbbreviatedName(cls)));
        }
        List<Class<?>> supertypes = ClassUtils.getAllSuperclasses(cls);
        supertypes.addAll(ClassUtils.getAllInterfaces(cls));
        supertypes.remove(Object.class);
        if (supertypes.isEmpty() || !ClassUtil.isConcrete(cls)) {
            throw new IllegalArgumentException("Not Subtype");
        }
        supertypes.add(cls);
        int count = 0;
        for (Class<?> supertype : supertypes) {
            JsonTypeInfo jsonTypeInfo = supertype.getAnnotation(JsonTypeInfo.class);
            if (jsonTypeInfo != null && jsonTypeInfo.use() != JsonTypeInfo.Id.NAME) {
                throw new IllegalArgumentException("Only Allow Use JsonTypeInfo.Id.NAME");
            }
            if (jsonTypeInfo != null && jsonTypeInfo.use() == JsonTypeInfo.Id.NAME) {
                count++;
            }
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Not Found JsonTypeInfo.ID.NAME");
        }
        return true;
    }

    public static void registerSubtypes(Class<?> cls) {
        if (checkSubtypes(cls)) {
            MAPPER.registerSubtypes(cls);
        }
    }

    public static String toJackSon(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static String toJson(Object value) {
        try {
            return toJackSon(value);
        } catch (IOException var3) {
            log.error("toJson error:{}", value, var3);
            return null;
        }
    }

    public static <T> T parseJackJson(String string, JavaType valueType) throws IOException {
        return StringUtils.isBlank(string) ? null : MAPPER.readValue(string, valueType);
    }

    public static <T> T parseJackJson(File file, Class<T> valueType) throws IOException {
        return parseJson(TextFileUtil.read(file), valueType);
    }

    public static <T> T parseJackJson(String string, Class<T> valueType) throws IOException {
        return StringUtils.isBlank(string) ? null : MAPPER.readValue(string, valueType);
    }

    public static <T> T parseJackJson(String string, TypeReference<T> valueType) throws IOException {
        return StringUtils.isBlank(string) ? null : MAPPER.readValue(string, valueType);
    }

    public static <T> T parseJson(String string, JavaType valueType) {
        try {
            return StringUtils.isBlank(string) ? null : MAPPER.readValue(string, valueType);
        } catch (IOException e) {
            log.error("JackSon to Object error,String={},valueType={}", string, valueType, e);
            return null;
        }
    }

    public static <T> T parseJson(String string, Class<T> valueType) {
        try {
            return parseJackJson(string, valueType);
        } catch (IOException e) {
            log.error("JackSon to Object error,String={},valueType={}", string, valueType, e);
            return null;
        }
    }

    public static <T> T parseJson(String string, TypeReference<T> valueType) {
        try {
            return parseJackJson(string, valueType);
        } catch (IOException e) {
            log.error("JackSon to Object error,string={},valueTypeRef={}", string, valueType, e);
            return null;
        }
    }

    public static <T> T parseJson(File file, Class<T> valueType) {
        try {
            return parseJackJson(file, valueType);
        } catch (IOException e) {
            log.error("JackSon to Object error,file={},valueType={}", file, valueType, e);
            return null;
        }
    }

    public static <T> String toJson(T object, TypeReference<T> valueTypeRef) {
        try {
            return toJackSon(object, valueTypeRef);
        } catch (Exception var3) {
            log.error("toT error: {}", object);
            return null;
        }
    }

    public static <T> String toJackSon(T object, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return MAPPER.writerFor(valueTypeRef).writeValueAsString(object);
    }

    public static <K, V> HashMap<K, V> parseHashMap(String string, Class<K> keyType, Class<V> valueType) {
        return parseMap(string, new HashMap<>(), keyType, valueType);
    }

    public static <K, V, T extends Map<K, V>> T parseMap(String string, T map, Class<K> keyType, Class<V> valueType) {
        Class<T> mapClass = (Class<T>) map.getClass();
        return parseMap(string, mapClass, keyType, valueType);
    }

    public static <K, V, T extends Map<K, V>> T parseMap(String string, Class<T> mapClass, Class<K> keyType, Class<V> valueType) {
        try {
            if (StringUtils.isBlank(string)) return null;
            return MAPPER.readValue(string, MAPPER.getTypeFactory().constructMapType(mapClass, keyType, valueType));
        } catch (IOException e) {
            log.error("JsonSon to Map Error", e);
            return null;
        }
    }

    public static <T extends java.util.Collection<E>, E> T parseCollection(String string, Class<T> collectionClass, Class<E> elementType) {
        try {
            if (StringUtils.isBlank(string))
                return null;
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementType);
            return MAPPER.readValue(string, type);
        } catch (IOException e) {
            log.error("JackSon to Object error, string={}, collectionClass={} elementClass={}", string, collectionClass, elementType, e);
            return null;
        }
    }
}
