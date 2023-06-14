package org.pot.gateway.net.netty;

import com.google.protobuf.Message;

public abstract class FrameMessage {
    public abstract boolean isProtoType(Class<? extends Message> protoType);

    public abstract String getProtoName();

    public abstract byte[] getProtoData();

    public abstract <T extends Message> T getProto();

    public abstract Class<? extends Message> getProtoType();

    protected abstract FrameMessage rebuild(Message message);
}
