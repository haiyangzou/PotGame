package org.pot.common.event;

import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.StringUtil;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class ProxyEventHandler implements EventHandler {
    private static final Map<String, FastClass> cache = new ConcurrentHashMap<>();
    protected final Object proxy;

    public ProxyEventHandler(Object proxy) {
        this.proxy = proxy;
    }

    public Object getProxy() {
        return proxy;
    }

    static ProxyEventHandler proxyEventHandler(Object proxy, Method method, Class<?> eventKind) throws InvocationTargetException {
        final String proxyClassName = getProxyEventHandlerClassName(proxy, method);
        FastClass proxyClass = cache.computeIfAbsent(proxyClassName, s -> {
            try {
                return proxyEventHandler0(proxyClassName, proxy, method);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        });
        if (proxyClass == null) {
            return null;
        }
        return (ProxyEventHandler) proxyClass.newInstance(new Class[]{Object.class}, new Object[]{proxy});
    }

    private static FastClass proxyEventHandler0(String proxyClassName, Object proxy, Method method) throws CannotCompileException, NotFoundException {
        Class<?> proxyClass;
        try {
            proxyClass = Class.forName(proxyClassName);
        } catch (ClassNotFoundException e) {
            ClassPool classPool = new ClassPool(true);
            classPool.appendClassPath(new ClassClassPath(ProxyEventHandler.class));
            CtClass ctClass = classPool.makeClass(proxyClassName);
            ctClass.setSuperclass(classPool.get(ProxyEventHandler.class.getName()));
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{classPool.get(Object.class.getName())}, ctClass);
            ctConstructor.setBody("{ super($1); }");
            ctClass.addConstructor(ctConstructor);
            StringBuilder handlerEventMethod = new StringBuilder();
            handlerEventMethod.append("public void handlerEvent(Object event) throws Exception {").append(StringUtil.getLineSeparator());
            handlerEventMethod.append("((").append(proxy.getClass().getName()).append(") proxy).").append(method.getName());
            handlerEventMethod.append("(");
            handlerEventMethod.append("(").append(method.getParameterTypes()[0].getName()).append(")").append("event");
            handlerEventMethod.append(");").append(StringUtil.getLineSeparator());
            handlerEventMethod.append("}");
            ctClass.addMethod(CtMethod.make(handlerEventMethod.toString(), ctClass));
            proxyClass = ctClass.toClass();
        }
        return FastClass.create(proxyClass);
    }

    private static String getProxyEventHandlerClassName(Object proxy, Method method) {
        return ProxyEventHandler.class.getSimpleName() + "$" + proxy.getClass().getName() + "$" + method.getName();
    }
}
