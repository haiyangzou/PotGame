package org.pot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.util.FilenameUtil;
import org.pot.core.engine.EngineInstance;

import java.io.File;

@Slf4j
public class Launcher implements Daemon {
    public static final class Env {
        @Getter
        private static String configLabel;
        @Getter
        private static Class<?> engineClass;
        @Getter
        private static String configFile;

        static {
            try {
                configLabel = System.getProperty("app.config.label", null);
                configFile = System.getProperty("app.config.file", null);
                if (configFile == null) {
                    throw new IllegalArgumentException("Launcher Config is Null");
                }
                if (StringUtils.isNotBlank(configLabel)) {
                    String part1 = StringUtils.substringBefore(configFile, ".");
                    String part2 = StringUtils.substringAfter(configFile, ".");
                    configFile = Constants.Env.getContextPath() + part1 + configFile + "." + part2;
                } else {
                    configFile = Constants.Env.getContextPath() + configFile;
                }
                configFile = FilenameUtil.formatPath(configFile);
                Configurations configurations = new Configurations();
                File propertiesFile = new File(Launcher.Env.getConfigFile());
                PropertiesConfiguration configuration = configurations.properties(propertiesFile);
                Constants.Env.setDebugOption(configuration.getString("app.debug"));
                Constants.Env.setWebOption(configuration.getString("app.web"));
                engineClass = Class.forName(configuration.getString("app.engine.class"));
                if (!AppEngine.class.isAssignableFrom(engineClass)) {
                    String info = String.format("Object named after %s must implement %s", engineClass, AppEngine.class.getName());
                    throw new IllegalArgumentException(info);
                }
                if (!ConstructorUtil.containsNoneParamConstructor(engineClass)) {
                    String info = String.format("Can't found none param constructor in %s", engineClass);
                    throw new IllegalArgumentException(info);
                }
                log.info("Launch app with config file:{}", Launcher.Env.getConfigFile());
            } catch (Throwable throwable) {
                throw new RuntimeException("Launcher Env Error", throwable);
            }
        }

    }

    @Getter
    private static DaemonContext daemonContext = null;

    @Override
    public void destroy() {
        log.info("Launcher is destroying...");
        log.info("Launcher is destroyed.");
    }

    @Override
    public final void init(DaemonContext daemonContext) throws Exception {
        log.info("Launcher is initializing...");
        Launcher.daemonContext = daemonContext;
        init(daemonContext.getArguments());
        log.info("Launcher is initialized.");
    }

    @Override
    public void start() throws Exception {
        log.info("Launcher is starting...");
        startup();
        log.info("Launcher is started...");
    }

    @Override
    public void stop() throws Exception {
        shutdown();
    }

    public static void bootstrap(String[] args) throws Exception {
        init(args);
        startup();
    }

    private static void init(final String[] args) throws Exception {
        EngineInstance.setInstance(ConstructorUtil.newObjectWithNonParam(Env.engineClass));
    }

    private static void startup() throws Exception {
        EngineInstance.getInstance().startup();
    }

    private static void shutdown() throws Exception {
        EngineInstance.getInstance().shutdown();
    }

}
