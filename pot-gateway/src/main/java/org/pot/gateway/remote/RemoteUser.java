package org.pot.gateway.remote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
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
import org.pot.message.protocol.login.LoginReconnectC2S;
import org.pot.message.protocol.login.LoginReqC2S;
import org.pot.message.protocol.login.LogoutReqC2S;

@Slf4j
public class RemoteUser {
    private final long createTime;
    @Getter
    private final IConnection<FrameCmdMessage> connection;
    @Getter
    private final LoginDataS2S loginDataS2S;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private volatile String signalFlagTick;

    public RemoteUser(Guest guest, LoginDataS2S loginDataS2S) {
        this.createTime = guest.getCreateTime();
        this.connection = guest.getConnection();
        this.connection.setImmediateFlush(false);
        this.loginDataS2S = loginDataS2S;
    }

    boolean tick() {
        long startTimeMillis = System.currentTimeMillis();
        IConnection<FrameCmdMessage> remoteUserConnection = connection;
        remoteUserConnection.flush();
        IConnection<FramePlayerMessage> remoteServerConnection = getRemoteServerConnection();
        if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
            disconnect(CommonErrorCode.SHUTDOWN_KICK);
            return true;
        }
        if (remoteUserConnection.isClosed()) {
            LogoutReqC2S logoutReqC2S = LogoutReqC2S.newBuilder().setGameUid(getGameUid()).build();
            remoteServerConnection.sendMessage(new FramePlayerMessage(getGameUid(), logoutReqC2S));
            return true;
        }
        FrameCmdMessage frameCmdMessage;
        while ((frameCmdMessage = remoteUserConnection.pollRecvMessage()) != null) {
            if (frameCmdMessage.isProtoType(LoginReqC2S.class) || frameCmdMessage.isProtoType(LoginReconnectC2S.class)) {
                ErrorCode err = ProtocolSupport.buildProtoErrorMsg(frameCmdMessage.getProtoType(), CommonErrorCode.ALREADY_LOGIN);
                remoteUserConnection.sendMessage(new FrameCmdMessage(err));
                continue;
            }
            remoteServerConnection.sendMessage(new FramePlayerMessage(getGameUid(), frameCmdMessage));
        }
        return false;
    }

    IConnection<FramePlayerMessage> getRemoteServerConnection() {
        RemoteServer remoteServer = GatewayEngine.getInstance().getRemoteServerManager().getRemoteServer(getServerId());
        if (remoteServer == null) {
            log.error("RemoteServer not exists.uid={}.server={}", getGameUid(), getServerId());
            return null;
        }
        IConnection<FramePlayerMessage> remoteServerConnection = remoteServer.getConnection(this);
        if (remoteServerConnection == null || remoteServerConnection.isClosed()) {
            log.error("RemoteServer  connection not exists.uid={}.server={}", getGameUid(), getServerId());
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
