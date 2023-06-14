package org.pot.gateway.guest;

import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.gateway.net.connection.IConnection;
import org.pot.gateway.net.netty.FrameCmdMessage;
import org.pot.message.protocol.DisConnectCode;
import org.pot.message.protocol.ErrorCode;
import org.pot.message.protocol.ProtocolSupport;

import com.google.protobuf.Message;

import lombok.Getter;

public class Guest {
    @Getter
    private final long createTime = System.currentTimeMillis();
    @Getter
    private final IConnection<FrameCmdMessage> connection;

    Guest(IConnection<FrameCmdMessage> connection) {
        this.connection = connection;
    }

    boolean ensureValid() {
        if (connection.isClosed()) {
            disconnect(CommonErrorCode.CONNECT_FAIL);
            return false;
        }
        if (isStayTooLongTime()) {
            disconnect(CommonErrorCode.IDLE_KICK);
            return false;
        }
        return true;
    }

    public void sendMessage(Message message) {
        connection.sendMessage(new FrameCmdMessage(message));

    }

    public void disconnect(IErrorCode errorCode) {
        disconnect(ProtocolSupport.buildProtoErrorMsg(errorCode));
    }

    public void disconnect(ErrorCode errorCode) {
        disconnect(DisConnectCode.newBuilder().setErrorCode(errorCode).build());
    }

    void disconnect(Message message) {
        connection.disconnect(new FrameCmdMessage(message));
    }

    public boolean isStayTooLongTime() {
        return System.currentTimeMillis() - createTime > 5 * 60000L;
    }
}
