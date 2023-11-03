package org.pot.game.engine.player.module.event;

import com.google.common.collect.Lists;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.pot.common.PotPackage;
import org.pot.common.anno.Ordinal;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.StringUtil;
import org.pot.game.engine.player.Player;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public interface PlayerEventsInitializer {
    PlayerEventsInitializer INITIALIZER = newPlayerEventsInitializer();

    static PlayerEventsInitializer newPlayerEventsInitializer() {
        String proxyClassName = PlayerEventsInitializer.class.getName() + "Impl";
        Class<?> proxyClass;
        try {
            proxyClass = Class.forName(proxyClassName);
        } catch (ClassNotFoundException e) {
            try {
                ClassPool classPool = new ClassPool(true);
                classPool.appendClassPath(new ClassClassPath(PlayerEventsInitializer.class));
                CtClass ctClass = classPool.makeClass(proxyClassName);
                ctClass.addInterface(classPool.get(PlayerEventsInitializer.class.getName()));
                classPool.importPackage(Player.class.getPackage().getName());
                String method = "public void initEvent(Player player) {" + StringUtil.getLineSeparator() +
                        addEvent() +
                        "}";
                ctClass.addMethod(CtMethod.make(method, ctClass));
                proxyClass = ctClass.toClass();
            } catch (Exception ex) {
                throw new ServiceException("PlayerEventsInitializer Error", ex);
            }
        }
        try {
            return (PlayerEventsInitializer) proxyClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ServiceException("PlayerEventsInitializer Error", e);
        }
    }

    @SuppressWarnings("unchecked")
    static String addEvent() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Class<? extends PlayerEventHandler>> eventHandlerTypes
                = Lists.newArrayList(ClassUtil.getSubTypeOf(PotPackage.class, PlayerEventHandler.class, ClassUtil::isConcrete));
        eventHandlerTypes.sort(Comparator.comparing(c -> {
            Ordinal ordinal = c.getAnnotation(Ordinal.class);
            return ordinal == null ? Ordinal.DEFAULT_ORDINAL : ordinal.value();
        }));
        for (Class<? extends PlayerEventHandler> eventHandlerType : eventHandlerTypes) {
            ConstructorUtil.requireNoneParamConstructor(eventHandlerType);
            Type superClass = eventHandlerType.getGenericSuperclass();
            Class<? extends PlayerEvent> eventType = ((Class<? extends PlayerEvent>) ((ParameterizedType) superClass).getActualTypeArguments()[0]);
            stringBuilder.append(StringUtil.format("player.eventComponent.registerPlayerEventHandler({}.class,new{}());", eventType.getName(), eventType.getName()));
            stringBuilder.append(StringUtil.getLineSeparator());
        }
        return stringBuilder.toString();
    }

    void initEvents(Player player);
}
