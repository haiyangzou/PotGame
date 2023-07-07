package org.pot.core.net.netty;

import com.google.protobuf.Message;
import lombok.Getter;

public class FramePlayerMessage extends FrameCmdMessage {
    @Getter
    private final long playerId;

    public FramePlayerMessage(Message proto) {
        this(0, proto);
    }

    public FramePlayerMessage(long playerId, Message proto) {
        super(proto);
        this.playerId = playerId;
    }

    public FramePlayerMessage(long playerId, FramePlayerMessage framePlayerMessage) {
        super(framePlayerMessage.getProtoName(), framePlayerMessage.getProtoData());
        this.playerId = playerId;

    }

    public FramePlayerMessage(long playerId, String protoName, byte[] protoData) {
        super(protoName, protoData);
        this.playerId = playerId;
    }

    @Override
    public FramePlayerMessage rebuild(Message message) {
        return new FramePlayerMessage(playerId, message);
    }
}
