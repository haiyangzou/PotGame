package org.pot.gateway.engine;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.pot.common.Constants;
import org.pot.core.engine.EngineConfig;

@Getter
public class GatewayEngineConfig extends EngineConfig {
    private final int userGroupSize = 20;
    private final int serverConnectionSize = 4;
    private final long guestReqExecSlowMillis = 20L;
    private final long guestReqWaitSlowMillis = 20L;
    private final long gatewayTickIntervalMillis = Constants.RUN_INTERVAL_MS;
    private final boolean gatewayRequestLogDisable = false;
    private final boolean gatewayResponseLogDisable = false;

    @Override
    protected void setProperties(Configuration config) {
        super.setProperties(config);

    }
}