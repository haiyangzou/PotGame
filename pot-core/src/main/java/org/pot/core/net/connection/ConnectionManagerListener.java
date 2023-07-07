package org.pot.core.net.connection;

import org.pot.core.net.netty.FrameMessage;

public interface ConnectionManagerListener<M extends FrameMessage> {
    boolean onAdd(final IConnection<M> connection) throws Exception;

    void onRemove(final IConnection<M> connection) throws Exception;
}
