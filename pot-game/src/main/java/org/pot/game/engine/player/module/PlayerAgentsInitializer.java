package org.pot.game.engine.player.module;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.pot.common.anno.Ordinal;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.util.StringUtil;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public interface PlayerAgentsInitializer {
    PlayerAgentsInitializer INITIALIZER = newPlayerAgentsInitializer();

    static PlayerAgentsInitializer newPlayerAgentsInitializer() {
        String proxyClassName = PlayerAgentsInitializer.class.getName() + "Impl";
        Class<?> proxyClass;
        try {
            proxyClass = Class.forName(proxyClassName);
        } catch (ClassNotFoundException e) {
            try {
                ClassPool classPool = new ClassPool(true);
                classPool.appendClassPath(new ClassClassPath(PlayerAgentsInitializer.class));
                CtClass ctClass = classPool.makeClass(proxyClassName);
                ctClass.addInterface(classPool.get(PlayerAgentsInitializer.class.getName()));
                classPool.importPackage(List.class.getPackage().getName());
                classPool.importPackage(Player.class.getPackage().getName());
                String method = "public List initAgents(Player player) {" + StringUtil.getLineSeparator() +
                        "   List list = new ArrayList();" + StringUtil.getLineSeparator() +
                        addAgents() +
                        "   return list;" + StringUtil.getLineSeparator() +
                        "}";
                ctClass.addMethod(CtMethod.make(method, ctClass));
                proxyClass = ctClass.toClass();
            } catch (Exception ex) {
                throw new ServiceException("PlayerAgentsInitializer Error", ex);
            }
        }
        try {
            return (PlayerAgentsInitializer) proxyClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ServiceException("PlayerAgentsInitializer Error", e);
        }
    }

    @SuppressWarnings("unchecked")
    static String addAgents() {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = FieldUtils.getAllFields(Player.class);
        Arrays.sort(fields, Comparator.comparingInt(f -> {
            Ordinal ordinal = f.getAnnotation(Ordinal.class);
            return ordinal == null ? Ordinal.DEFAULT_ORDINAL : ordinal.value();
        }));
        for (Field field : fields) {
            if (ClassUtils.isAssignable(field.getType(), PlayerAgentAdapter.class)) {
                stringBuilder.append(StringUtil.format("    list.add(player.{});", field.getName()));
                stringBuilder.append(StringUtil.getLineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    List<PlayerAgentAdapter> initAgents(Player player);
}
