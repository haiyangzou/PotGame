package org.pot.core.engine;

import lombok.Getter;
import org.pot.common.config.ExecutorConfig;
import org.pot.common.net.ipv4.FireWall;

@Getter
public class EngineConfig {
    private ExecutorConfig asyExecutorConfig;
    private FireWall tcpFirewall = FireWall.empty();
    private FireWall webFirewall = FireWall.lan();

}
