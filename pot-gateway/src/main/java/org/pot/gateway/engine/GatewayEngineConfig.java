package org.pot.gateway.engine;

import org.pot.core.engine.EngineConfig;

import lombok.Getter;

@Getter
public class GatewayEngineConfig extends EngineConfig {
    private long guestReqExecSlowMillis = 20L;
    private long guestReqWaitSlowMillis = 20L;
}