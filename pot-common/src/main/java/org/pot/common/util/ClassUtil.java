package org.pot.common.util;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.file.FileType;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Predicate;

public class ClassUtil {
    public static String getAbbreviatedName(Class<?> type) {
        return getAbbreviatedName(type.getName(),1);
    }
    public static String getAbbreviatedName(String stacktrace, int depth) {
        if (stacktrace == null || stacktrace.length() == 0) {
            return StringUtils.EMPTY;
        }
        if (depth < 1) {
            depth = 1;
        }
        int hold = StringUtils.lastOrdinalIndexOf(stacktrace, ".", depth);
        if (hold <= 0) {
            return stacktrace;
        }
        final char[] abbre = stacktrace.toCharArray();
        int target = 0;
        int source = 0;
        while (source < abbre.length) {
            int runAheadTarget = target;
            while (source < abbre.length && abbre[source] != '.') {
                abbre[runAheadTarget++] = abbre[source++];
            }
            ++target;
            if (source > hold || target > runAheadTarget) {
                target = runAheadTarget;
            }
            if (source < abbre.length) {
                abbre[target++] = abbre[source++];
            }
        }
        return new String(abbre, 0, target);
    }
    public static boolean isConcrete(Class<?> type){
        int mod = type.getModifiers();
        return !Modifier.isInterface(mod) && !Modifier.isAbstract(mod);
    }
    public static <T,A extends Annotation> Set<Class<? extends T>> getSubTypeOf(final Class scope, final Class<T> type, Predicate<Class<? extends T>> filter){
        return getSubTypeOf(scope.getPackage(),type,null,filter);
    }
    public static <T,A extends Annotation> Set<Class<? extends T>> getSubTypeOf(final Package scope, final Class<T> type, Class<A> annotation,Predicate<Class<? extends T>> filter){
        return getSubTypeOf(scope.getName(),type,null,filter);
    }
    public static <T,A extends Annotation> Set<Class<? extends T>> getSubTypeOf(final String scope, final Class<T> type, Class<A> annotation, Predicate<Class<? extends T>> filter){
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setExpandSuperTypes(true);
        configurationBuilder.setScanners(new SubTypesScanner());
        configurationBuilder.setInputsFilter(new FilterBuilder.Include(FileType.CLASS.getPatternString()));
        configurationBuilder.setUrls(ClasspathHelper.forPackage(scope));
        final Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<? extends T>> subTypeSet = reflections.getSubTypesOf(type);
        subTypeSet.removeIf(clazz->(filter!=null && !filter.test(clazz) ||(annotation!=null && !clazz.isAnnotationPresent(annotation))));
        return ImmutableSet.copyOf(subTypeSet);
    }
}