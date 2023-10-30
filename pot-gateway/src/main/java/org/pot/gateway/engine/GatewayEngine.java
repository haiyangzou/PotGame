package org.pot.gateway.engine;

import lombok.Getter;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.gateway.remote.RemoteServerManager;
import org.pot.gateway.remote.RemoteUserManager;

public class GatewayEngine extends AppEngine<GatewayEngineConfig> {
    public static GatewayEngine getInstance() {
        return (GatewayEngine) EngineInstance.getInstance();
    }

    @Getter
    private RemoteUserManager remoteUserManager;
    @Getter
    private RemoteServerManager remoteServerManager;

    protected GatewayEngine(Class<GatewayEngineConfig> configClass) throws Exception {
        super(configClass);
    }

    @Override
    protected void doStart() throws Throwable {
        remoteUserManager = new RemoteUserManager();
        remoteServerManager = new RemoteServerManager();
    }

    @Override
    protected void doStop() {

    }

}
