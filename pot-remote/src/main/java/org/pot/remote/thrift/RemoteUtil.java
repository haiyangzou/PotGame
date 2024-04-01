package org.pot.remote.thrift;

import lombok.extern.slf4j.Slf4j;
import org.pot.PotPackage;
import org.pot.common.communication.server.ServerType;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.MapUtil;
import org.pot.remote.thrift.define.IRemote;
import org.pot.remote.thrift.define.RemoteServerType;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class RemoteUtil {
    private static Map<ServerType, Set<Class<? extends IRemote>>> remoteServiceInterfaceMap;

    static {
        Map<ServerType, Set<Class<? extends IRemote>>> map = new HashMap<>();
        Set<Class<? extends IRemote>> remoteServiceInterfaces = ClassUtil.getSubTypeOf(PotPackage.class, IRemote.class, ClassUtil::isConcrete);
        for (Class<? extends IRemote> remoteServiceInterface : remoteServiceInterfaces) {
            Map<String, Integer> duplicated = new HashMap<>();
            Method[] methods = remoteServiceInterface.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                int count = method.getParameterCount();
                if (duplicated.getOrDefault(name, -1) == count) {
                    throw new IllegalArgumentException("Duplicated" + remoteServiceInterface.getSimpleName() + "#" + name);
                }
                duplicated.putIfAbsent(name, count);
            }
            RemoteServerType remoteServerType = remoteServiceInterface.getAnnotation(RemoteServerType.class);
            if (remoteServerType == null) {
                continue;
            }
            ServerType[] serverTypes = remoteServerType.value();
            for (ServerType serverType : serverTypes) {
                map.computeIfAbsent(serverType, k -> new LinkedHashSet<>()).add(remoteServiceInterface);
            }
        }
        remoteServiceInterfaceMap = MapUtil.immutableMapSet(map);
    }

    public static void init() {

    }

    public static Set<Class<? extends IRemote>> getRemoteServiceInterface(ServerType serverType) {
        return remoteServiceInterfaceMap.getOrDefault(serverType, Collections.emptySet());
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends IRemote> getRemoteServiceInterface(Class<? extends IRemote> remoteConcreteClass) {
        if (!ClassUtil.isConcrete(remoteConcreteClass)) {
            throw new IllegalStateException("Not Concrete Remote" + remoteConcreteClass.getName());
        }
        Class<?>[] interfaces = remoteConcreteClass.getInterfaces();
        if (Arrays.stream(interfaces).filter(inf -> IRemote.class != inf).filter(IRemote.class::isAssignableFrom).count() > 1) {
            throw new IllegalStateException("Multiple Remote Service" + remoteConcreteClass.getName());
        }
        for (Class<?> inf : interfaces) {
            if (IRemote.class != inf && IRemote.class.isAssignableFrom(inf)) {
                return (Class<? extends IRemote>) inf;
            }
        }
        throw new IllegalStateException("Not Remote Service Impl" + remoteConcreteClass.getName());
    }

}
