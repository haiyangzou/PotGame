package org.pot.gateway.engine;

import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;

public class GatewayEngine extends AppEngine<GatewayEngineConfig> {
    public static GatewayEngine getInstance() {
        return (GatewayEngine) EngineInstance.getInstance();
    }

    protected GatewayEngine(Class<GatewayEngineConfig> configClass) throws Exception {
        super(configClass);
    }

    @Override
    protected void doStart() throws Throwable {

    }

    @Override
    protected void doStop() {

    }

}
