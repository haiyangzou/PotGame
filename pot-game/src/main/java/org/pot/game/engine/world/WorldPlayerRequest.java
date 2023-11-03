package org.pot.game.engine.world;

import com.google.protobuf.Message;
import lombok.Getter;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.net.protocol.MessageSender;
import org.pot.game.engine.player.PlayerManager;
import org.pot.message.protocol.ProtocolSupport;

public abstract class WorldPlayerRequest implements MessageSender {
    @Getter
    private final long createTime = System.currentTimeMillis();
    @Getter
    private final long playerId;
    @Getter
    private final Message request;

    public WorldPlayerRequest(long playerId, Message request) {
        this.playerId = playerId;
        this.request = request;
    }

    @Override
    public void sendMessage(Message message) {
        PlayerManager.getInstance().sendMessage(playerId, message);
    }

    public void sendError(IErrorCode errorCode) {
        sendMessage(ProtocolSupport.buildProtoErrorMsg(request.getClass(), errorCode));
    }

    protected void rollbackOnError(IErrorCode error) {

    }

    public abstract IErrorCode handle();
}
