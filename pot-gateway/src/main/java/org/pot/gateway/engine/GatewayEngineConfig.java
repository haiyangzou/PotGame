package org.pot.gateway.engine;

import lombok.Getter;
import org.pot.common.Constants;
import org.pot.core.engine.EngineConfig;

@Getter
public class GatewayEngineConfig extends EngineConfig {
    private int userGroupSize = 20;
    private int serverConnectionSize = 4;
    private long guestReqExecSlowMillis = 20L;
    private long guestReqWaitSlowMillis = 20L;
    private long gatewayTickIntervalMillis = Constants.RUN_INTERVAL_MS;

}