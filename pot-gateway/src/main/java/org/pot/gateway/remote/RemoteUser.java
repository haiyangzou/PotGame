package org.pot.gateway.remote;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FrameCmdMessage;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.gateway.engine.GatewayEngine;
import org.pot.gateway.guest.Guest;
import org.pot.message.protocol.DisConnectCode;
import org.pot.message.protocol.ErrorCode;
import org.pot.message.protocol.ProtocolSupport;
import org.pot.message.protocol.login.LoginDataS2S;

@Slf4j
public class RemoteUser {
    private final long createTime;
    private final IConnection<FrameCmdMessage> connection;
    private final LoginDataS2S loginDataS2S;

    public RemoteUser(Guest guest, LoginDataS2S loginDataS2S) {
        this.createTime = guest.getCreateTime();
        this.connection = guest.getConnection();
        this.connection.setImmediateFlush(false);
        this.loginDataS2S = loginDataS2S;
    }

    boolean tick() {
        return false;
    }

    IConnection<FramePlayerMessage> getRemoteServerConnection() {
        RemoteServer remoteServer = GatewayEngine.getInstance().getRemoteServerManager().getRemoteServer(getServerId());
        if (remoteServer == null) {

        }
        IConnection<FramePlayerMessage> remoteServerConnection = remoteServer.getConnection(this);
        if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
            return null;
        }
        return remoteServerConnection;
    }

    public void disconnect(IErrorCode errorCode) {
        disconnect(ProtocolSupport.buildProtoErrorMsg(errorCode));
    }

    public void disconnect(ErrorCode errorCode) {
        disconnect(DisConnectCode.newBuilder().setErrorCode(errorCode).build());
    }

    public void disconnect(DisConnectCode disconnectCode) {
        connection.disconnect(new FrameCmdMessage(disconnectCode));
    }

    public int getServerId() {
        return loginDataS2S.getServerId();
    }

    public long getGameUid() {
        return loginDataS2S.getGameUid();
    }
}
