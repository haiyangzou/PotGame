package org.pot.game.engine.gate;

import lombok.Getter;
import lombok.Setter;
import org.pot.common.communication.server.ServerId;

@Getter
@Setter
public class TunnelVisaData {
    private volatile long timeout;
    private volatile ServerId redirectToServerId;
    private volatile ServerId targetServerId;
}
