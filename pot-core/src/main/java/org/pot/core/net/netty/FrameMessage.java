package org.pot.core.net.netty;

import com.google.protobuf.Message;

import lombok.Getter;

public abstract class FrameMessage {
    @Getter
    private final long createTime = System.currentTimeMillis();

    public abstract boolean isProtoType(Class<? extends Message> protoType);

    public abstract String getProtoName();

    public abstract byte[] getProtoData();

    public abstract <T extends Message> T getProto();

    public abstract Class<? extends Message> getProtoType();

    protected abstract FrameMessage rebuild(Message message);

    @SuppressWarnings("unchecked")
    public final <M extends FrameMessage> M renew(Message message) {
        return (M) rebuild(message);
    }
}
