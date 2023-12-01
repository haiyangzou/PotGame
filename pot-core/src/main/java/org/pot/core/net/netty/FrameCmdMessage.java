package org.pot.core.net.netty;

import org.apache.commons.lang3.StringUtils;
import org.pot.message.protocol.ProtocolSupport;

import com.google.protobuf.Message;

public class FrameCmdMessage extends FrameMessage {
    protected final String protoName;
    protected volatile byte[] protoData;
    protected volatile Message proto;

    public FrameCmdMessage(Message proto) {
        this.proto = proto;
        this.protoName = ProtocolSupport.name(proto.getClass());
    }

    public FrameCmdMessage(FramePlayerMessage framePlayerMessage) {
        this.protoName = framePlayerMessage.getProtoName();
        this.protoData = framePlayerMessage.getProtoData();
    }

    public FrameCmdMessage(String protoName, byte[] protoData) {
        this.protoName = protoName;
        this.protoData = protoData;
    }

    @Override
    public boolean isProtoType(Class<? extends Message> protoType) {
        return StringUtils.equals(protoName, ProtocolSupport.name(protoType));
    }

    @Override
    public String getProtoName() {
        return protoName;
    }

    @Override
    public byte[] getProtoData() {
        byte[] temp = protoData;
        if (temp != null) {
            return temp;
        }
        return protoData = proto.toByteArray();
    }

    @Override
    public <T extends Message> T getProto() {
        Message temp = proto;
        if (temp != null) {
            return (T) temp;
        }
        return (T) (proto = ProtocolSupport.parse(protoName, protoData));
    }

    @Override
    public Class<? extends Message> getProtoType() {
        return ProtocolSupport.type(protoName);
    }

    @Override
    protected FrameMessage rebuild(Message message) {
        return new FrameCmdMessage(message);
    }

}
