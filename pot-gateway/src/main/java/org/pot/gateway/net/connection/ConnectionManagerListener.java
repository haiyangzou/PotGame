package org.pot.gateway.net.connection;

import org.pot.gateway.net.netty.FrameMessage;

public interface ConnectionManagerListener<M extends FrameMessage> {
    boolean onAdd(final IConnection<M> connection) throws Exception;

    void onRemove(final IConnection<M> connection) throws Exception;
}
