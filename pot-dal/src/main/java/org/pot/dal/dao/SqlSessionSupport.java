package org.pot.dal.dao;

import com.google.common.collect.ImmutableMap;
import org.pot.PotPackage;
import org.pot.common.util.ClassUtil;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SqlSessionSupport {
    private static final Map<Class<? extends SessionMapper>, FastClass> sessionMapperFastClassMap;

    static {
        Map<Class<? extends SessionMapper>, FastClass> tempSessionMapperFastClassMap = new HashMap<>();
        try {
            Set<Class<? extends SessionMapper>> sessionMapperClassSet = ClassUtil.getSubTypeOf(
                    PotPackage.class, SessionMapper.class, subType -> subType != SessionMapperAdapter.class);
            for (Class sessionMapperClass : sessionMapperClassSet) {
                SessionMapperAdapter sessionMapper = null;
                if (ClassUtil.isConcrete(sessionMapperClass)) {
                    FastClass fastClass = FastClass.create(sessionMapperClass);
                    tempSessionMapperFastClassMap.putIfAbsent(sessionMapperClass, fastClass);
                } else {
                    Set<Class<?>> sessionMapperImplClassSet = ClassUtil.getSubTypeOf(
                            PotPackage.class, sessionMapperClass,
                            subType -> ClassUtil.isConcrete(subType) && SessionMapperAdapter.class.isAssignableFrom(subType)
                    );
                    int size = sessionMapperImplClassSet.size();
                    if (size <= 0) {
                        throw new IllegalStateException(ClassUtil.getAbbreviatedName(sessionMapperClass) + "implementor not found");
                    }
                    if (size > 1) {
                        throw new IllegalStateException(ClassUtil.getAbbreviatedName(sessionMapperClass) + "implementor too many");
                    }
                    for (Class sessionMapperImplClass : sessionMapperImplClassSet) {
                        FastClass fastClass = FastClass.create(sessionMapperImplClass);
                        tempSessionMapperFastClassMap.putIfAbsent(sessionMapperClass, fastClass);
                        sessionMapper = (SessionMapperAdapter) fastClass.newInstance();
                    }
                    if (sessionMapper == null) {
                        throw new IllegalStateException(ClassUtil.getAbbreviatedName(sessionMapperClass) + "is null");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessionMapperFastClassMap = ImmutableMap.copyOf(tempSessionMapperFastClassMap);
    }

    static <Mapper extends SessionMapper> Mapper getSessionMapper(Class<Mapper> mapperClass, SqlSession sqlSession) {
        FastClass fastClass = sessionMapperFastClassMap.get(mapperClass);
        if (fastClass == null) {
            throw new IllegalStateException("mapper for class" + mapperClass.getName() + "not found");
        }
        try {
            SessionMapper instance = (SessionMapper) fastClass.newInstance();
            instance.injectSqlSession(sqlSession);
            return (Mapper) instance;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
