package org.pot.game.gate;

import lombok.Getter;
import lombok.Setter;
import org.pot.common.communication.server.ServerId;

@Getter
@Setter
public class TunnelVisaData {
    private volatile long timeout;
    private volatile ServerId redirectToServerId;
    private volatile ServerId targetServerId;
    private volatile TunnelVisaType visaType;
    private volatile ServerId sourceServerId;
    protected volatile int sourcePointId;
    protected volatile int targetPointId;
}
