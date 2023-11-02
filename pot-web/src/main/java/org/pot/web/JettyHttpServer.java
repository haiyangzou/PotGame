package org.pot.web;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.pot.common.Constants;
import org.pot.common.PotPackage;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.config.JettyConfig;
import org.pot.common.util.ClassUtil;
import org.pot.common.util.MapUtil;
import org.pot.core.engine.IHttpServer;

import javax.servlet.http.HttpServlet;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class JettyHttpServer extends Thread implements IHttpServer {
    private final Server server;
    @Getter
    private final StatisticsHandler statisticsHandler;

    private final Map<String, LongAdder> totalRequest = new ConcurrentHashMap<>();

    public JettyHttpServer(JettyConfig jettyConfig) {
        if (jettyConfig.getPort() <= 0) {
            throw new RuntimeException("jetty server port must be positive");
        }
        this.setName(JettyHttpServer.class.getSimpleName());
        final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setSessionHandler(new SessionHandler());
        servletContextHandler.setServletHandler(new ServletHandler());
        final Set<String> ignoreRequestLogPaths = new HashSet<>();
        Map<String, DetectedHttpService> httpServices = doScanHttpServices();
        for (DetectedHttpService httpService : httpServices.values()) {
            servletContextHandler.getServletHandler().addServletWithMapping(httpService.type, httpService.path);
            if (httpService.ignoreRequestLog) {
                ignoreRequestLogPaths.add(httpService.path);
            }
        }
        final CustomRequestLog customRequestLog = new CustomRequestLog(log::info, CustomRequestLog.EXTENDED_NCSA_FORMAT);
        customRequestLog.setIgnorePaths(ignoreRequestLogPaths.toArray(new String[0]));
        final RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(customRequestLog);
        ResourceHandler resourceHandler = new ResourceHandler();
        URL webStaticUrl = getClass().getClassLoader().getResource(Constants.WEB_STATIC);
        resourceHandler.setBaseResource(Resource.newResource(webStaticUrl));
        ContextHandler contextHandler = new ContextHandler("/" + Constants.WEB_STATIC);
        contextHandler.setHandler(resourceHandler);

        this.statisticsHandler = new StatisticsHandler();
        this.statisticsHandler.setHandler(new HandlerCollection(requestLogHandler, new ContextHandlerCollection(servletContextHandler, contextHandler)));
        final int idleTimeout = 1000;
        this.server = new Server(new QueuedThreadPool(jettyConfig.getThreadPoolMaxSize(), jettyConfig.getThreadPoolMaxSize(), idleTimeout));
        this.server.setHandler(statisticsHandler);
        ServerConnector serverConnector = new ServerConnector(server);
        if (StringUtils.isNotBlank(jettyConfig.getHost())) {
            serverConnector.setHost(jettyConfig.getHost());
        }
        serverConnector.setPort(jettyConfig.getPort());
        serverConnector.setIdleTimeout(TimeUnit.SECONDS.toMillis(jettyConfig.getTimeoutSeconds()));
        serverConnector.addFirstConnectionFactory(new ProxyConnectionFactory());
        this.server.setConnectors(new Connector[]{serverConnector});
    }

    private Map<String, DetectedHttpService> doScanHttpServices() {
        Set<Class<? extends HttpServlet>> servletClazzSet = ClassUtil.getSubTypeOf(
                PotPackage.class, HttpServlet.class, EnableHttpService.class
        );
        Map<String, DetectedHttpService> serviceMap = MapUtil.newHashMap();
        for (Class<? extends HttpServlet> servletClazz : servletClazzSet) {
            EnableHttpService annotation = servletClazz.getAnnotation(EnableHttpService.class);
            String path = StringUtils.strip(annotation.path());
            if (StringUtils.isBlank(path)) {
                continue;
            }
            DetectedHttpService prev = serviceMap.get(path);
            if (prev != null) {
                continue;
            }
            serviceMap.put(path, new DetectedHttpService(servletClazz, annotation));
        }
        return Collections.unmodifiableMap(serviceMap);
    }

    @Override
    public void run() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startup() {
        this.start();
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, this.server::isStarted);
    }

    @Override
    public void shutdown() {
        if (server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("http server not running");
        }
    }

    @Override
    public int getRequests() {
        return statisticsHandler.getRequests();
    }

    @Override
    public int getRequestActive() {
        return statisticsHandler.getRequestsActive();
    }

    @Override
    public Map<String, Object> getStatus() {
        return null;
    }

    @Override
    public void onAcceptRequest(String requestURI, String requestIP) {
        totalRequest.computeIfAbsent(requestURI, k -> new LongAdder()).increment();
    }
}
