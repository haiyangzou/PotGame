package org.pot.remote.thrift.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.pot.PotPackage;
import org.pot.common.Constants;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.config.RemoteServerConfig;
import org.pot.common.relect.FieldUtil;
import org.pot.common.spring.SpringContextUtils;
import org.pot.common.util.ClassUtil;
import org.pot.remote.thrift.Remote;
import org.pot.remote.thrift.RemoteUtil;
import org.pot.remote.thrift.define.IRemote;
import org.pot.remote.thrift.define.RemoteServerType;
import org.pot.remote.thrift.define.alive.IKeepAliveRemote;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RemoteServer {
    private volatile TServer tServer;
    private final RemoteServerConfig remoteServerConfig;
    private final RemoteServerHandler remoteServerHandler = new RemoteServerHandler();

    public RemoteServer(RemoteServerConfig remoteServerConfig) {
        this.remoteServerConfig = remoteServerConfig;
    }

    public void addHandler(String serviceName, Object handler) {
        remoteServerHandler.addHandler(serviceName, handler);
    }

    public void addServerTypeHandlerIfAbsent(ServerType... serverTypes) {
        Set<ServerType> serverTypeSet = new LinkedHashSet<>(Arrays.asList(serverTypes));
        for (ServerType serverType : serverTypeSet) {
            ApplicationContext springContext = SpringContextUtils.getApplicationContext();
            if (springContext == null) {
                Set<Class<? extends IRemote>> remoteServiceClasses = ClassUtil.getSubTypeOf(PotPackage.class, IRemote.class, ClassUtil::isConcrete);
                for (Class<? extends IRemote> remoteServiceClass : remoteServiceClasses) {
                    try {
                        if (!FieldUtil.getAllFieldsList(remoteServiceClass).isEmpty()) {
                            log.warn("thrift server handler:{}", remoteServiceClass.getName());
                        }
                        Class<? extends IRemote> remoteServiceInterface = RemoteUtil.getRemoteServiceInterface(remoteServiceClass);
                        if (ArrayUtils.contains(remoteServiceInterface.getAnnotation(RemoteServerType.class).value(), serverType)) {
                            if (remoteServerHandler.containsRemote(remoteServiceInterface)) {
                                log.warn("thrift dup:{}", remoteServiceClass.getName());
                            } else {
                                FastClass remoteFastClass = FastClass.create(remoteServiceClass);
                                IRemote remote = (IRemote) remoteFastClass.newInstance();
                                addHandler(remoteServiceInterface.getName(), remote);
                            }
                        }
                    } catch (Throwable e) {
                        log.error("thrift server handler error:{}", remoteServiceClass.getName(), e);
                    }
                }
                Set<Class<? extends IKeepAliveRemote>> aliveServiceClasses = ClassUtil.getSubTypeOf(PotPackage.class, IKeepAliveRemote.class, ClassUtil::isConcrete);
                for (Class<? extends IKeepAliveRemote> aliveServiceClass : aliveServiceClasses) {
                    try {
                        FastClass remoteFastClass = FastClass.create(aliveServiceClass);
                        IKeepAliveRemote remote = (IKeepAliveRemote) remoteFastClass.newInstance();
                        addHandler(IKeepAliveRemote.class.getName(), remote);
                    } catch (Throwable e) {
                        log.error("thrift server handler error:{}", aliveServiceClass.getName(), e);
                    }
                }
            } else {
                Map<String, IRemote> remoteHandlerMap = springContext.getBeansOfType(IRemote.class);
                for (IRemote remote : remoteHandlerMap.values()) {
                    Class<? extends IRemote> remoteServiceInterface = RemoteUtil.getRemoteServiceInterface(remote.getClass());
                    if (ArrayUtils.contains(remoteServiceInterface.getAnnotation(RemoteServerType.class).value(), serverType)) {
                        addHandler(remoteServiceInterface.getName(), remote);
                    }
                }
            }
            Set<Class<? extends IRemote>> remoteServiceInterfaces = RemoteUtil.getRemoteServiceInterface(serverType);
            for (Class<? extends IRemote> remoteServiceInterface : remoteServiceInterfaces) {
                if (!remoteServerHandler.containsRemote(remoteServiceInterface)) {
                    log.error("thrift missing:{}", remoteServiceInterface.getName());
                }
            }
        }
    }

    public void start() throws Exception {
        startServer(remoteServerConfig);
    }

    private void startServer(RemoteServerConfig remoteServerConfig) throws TTransportException {
        TNonblockingServerSocket socket = new TNonblockingServerSocket(
                new TNonblockingServerSocket.NonblockingAbstractServerSocketArgs()
                        .clientTimeout(remoteServerConfig.getClientTimeout())
                        .bindAddr(getBindAddress(remoteServerConfig))
                        .backlog(remoteServerConfig.getBacklog())
        );
        TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(socket);
        serverArgs.maxReadBufferBytes = remoteServerConfig.getMaxReadBufferBytes();
        serverArgs.processor(new Remote.Processor<>(remoteServerHandler));
        serverArgs.selectorThreads(remoteServerConfig.getSelectorThreads());
        serverArgs.acceptQueueSizePerThread(remoteServerConfig.getAcceptQueueSizePerThread());
        serverArgs.executorService(new ThreadPoolExecutor(
                remoteServerConfig.getWorkerThreadsMin(),
                remoteServerConfig.getWorkerThreadsMax(),
                remoteServerConfig.getWorkerKeepAliveSeconds(), TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(remoteServerConfig.getWorkerQueueMaxSize()),
                new ThreadFactoryBuilder().setNameFormat("ThriftWorker-%d").build()
        ));
        tServer = new TThreadedSelectorServer(serverArgs);
        new Thread(() -> tServer.serve(), "ThriftServer").start();
        log.info("thrift server started on {},{}", remoteServerConfig.getHost(), remoteServerConfig.getPort());
    }

    private InetSocketAddress getBindAddress(RemoteServerConfig remoteServerConfig) {
        InetSocketAddress address;
        if (StringUtils.isBlank(remoteServerConfig.getHost()) || "*".equals(remoteServerConfig.getHost())) {
            address = new InetSocketAddress(remoteServerConfig.getPort());
            log.warn("thrift server not bind");
            log.info("thrift server bind on port:{}", remoteServerConfig.getPort());
        } else {
            address = new InetSocketAddress(remoteServerConfig.getHost(), remoteServerConfig.getPort());
            log.info("thrift server bind on port:{}", remoteServerConfig.getPort());
        }
        return address;
    }

    public void stop() {
        if (tServer != null) {
            tServer.stop();
            ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> !tServer.isServing());
        }
    }

    public Object localInvoke(String serviceName, Method method, Object[] args) {
        return remoteServerHandler.localCall(serviceName, method, args);
    }
}
