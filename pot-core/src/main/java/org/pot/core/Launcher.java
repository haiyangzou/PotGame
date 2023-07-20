package org.pot.core;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.pot.common.relect.ConstructorUtil;
import org.pot.core.engine.EngineInstance;

import lombok.Getter;

public class Launcher implements Daemon {
    public static final class Env {
        @Getter
        private static Class<?> engineClass;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(DaemonContext arg0) throws DaemonInitException, Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
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

}
