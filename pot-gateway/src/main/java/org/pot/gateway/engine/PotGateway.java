package org.pot.gateway.engine;

import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;

public class PotGateway extends AppEngine<GatewayEngineConfig> {
    public static PotGateway getInstance() {
        return (PotGateway) EngineInstance.getInstance();
    }

    protected PotGateway(Class<GatewayEngineConfig> configClass) throws Exception {
        super(configClass);
    }

}
