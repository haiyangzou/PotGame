package org.pot.game.gate;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.game.engine.GameEngine;
import org.pot.message.protocol.DisConnectCode;
import org.pot.message.protocol.ProtocolSupport;
import org.pot.message.protocol.login.LogoutReqC2S;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerSession {
    private static final long IDLE_MILLS = TimeUnit.MINUTES.toMillis(10);
    @Getter
    private final long uid;
    @Getter
    private volatile long lastActiveTimeMills;
    @Getter
    private volatile boolean established;
    @Getter
    private volatile boolean disconnected;
    @Getter
    private final Deque<FramePlayerMessage> recvMessageQueue = new LinkedBlockingDeque<>();
    @Getter
    private final Deque<FramePlayerMessage> sendMessageQueue = new LinkedBlockingDeque<>();

    public PlayerSession(long uid) {
        this.uid = uid;
        this.initialize();
    }

    synchronized void initialize() {
        this.established = false;
        this.disconnected = false;
        recvMessageQueue.clear();
        sendMessageQueue.clear();
        this.lastActiveTimeMills = System.currentTimeMillis();
    }

    public boolean send(Message message) {
        if (this.disconnected) return false;
        if (!this.established) return false;
        int size = this.sendMessageQueue.size();
        if (size > 1000) {
            this.disconnect(CommonErrorCode.CONNECT_FAIL);
            return false;
        } else {
            this.sendMessageQueue.add(new FramePlayerMessage(this.uid, message));
            this.lastActiveTimeMills = System.currentTimeMillis();
            return true;
        }
    }

    public void disconnect(IErrorCode errorCode) {
        DisConnectCode.Builder builder = DisConnectCode.newBuilder();
        builder.setErrorCode(ProtocolSupport.buildProtoErrorMsg(errorCode));
        this.disconnect(builder.build());
    }

    public synchronized void disconnect(Message message) {
        if (this.disconnected) return;
        this.disconnected = true;
        this.sendMessageQueue.clear();
        this.sendMessageQueue.add(new FramePlayerMessage(this.uid, message));
        this.recvMessageQueue.clear();
        LogoutReqC2S logoutReqC2S = LogoutReqC2S.newBuilder().setGameUid(this.uid).build();
        this.recvMessageQueue.add(new FramePlayerMessage(this.uid, logoutReqC2S));
    }

    public FramePlayerMessage pollSendMessage() {
        FramePlayerMessage message = this.sendMessageQueue.poll();
        if (message != null) this.lastActiveTimeMills = System.currentTimeMillis();
        return message;
    }

    public FramePlayerMessage pollRecvMessage() {
        FramePlayerMessage message = this.recvMessageQueue.poll();
        if (message != null) this.lastActiveTimeMills = System.currentTimeMillis();
        return message;
    }

    public boolean isIdle() {
        if (this.isOnline()) return false;
        return System.currentTimeMillis() - this.lastActiveTimeMills > IDLE_MILLS;
    }

    public boolean isOnline() {
        return this.established && !this.disconnected;
    }

    public void establish(Message message) {
        this.sendMessageQueue.clear();
        this.recvMessageQueue.clear();
        this.recvMessageQueue.add(new FramePlayerMessage(this.uid, message));
        this.lastActiveTimeMills = System.currentTimeMillis();
        established = true;
    }

    void recv(FramePlayerMessage framePlayerMessage) {
        if (this.disconnected) return;
        if (!this.established) return;
        int size = this.sendMessageQueue.size();
        if (size > GameEngine.getInstance().getConfig().getPlayerSessionRecvQueueMaxSize()) {
            log.error("Player Session Recv QUeue Overflow. uid = {}.size={}", this.uid, size);
            this.disconnect(CommonErrorCode.CONNECT_FAIL);
        } else {
            this.recvMessageQueue.add(framePlayerMessage);
            this.lastActiveTimeMills = System.currentTimeMillis();
        }
    }

    synchronized void close() {
        if (this.disconnected) return;
        this.disconnected = true;
        this.sendMessageQueue.clear();
        FramePlayerMessage framePlayerMessage = recvMessageQueue.peekLast();
        if (framePlayerMessage == null || !framePlayerMessage.isProtoType(LogoutReqC2S.class)) {
            LogoutReqC2S logoutReqC2S = LogoutReqC2S.newBuilder().setGameUid(this.uid).build();
            this.recvMessageQueue.add(new FramePlayerMessage(this.uid, logoutReqC2S));
        }
        this.lastActiveTimeMills = System.currentTimeMillis();
    }


}
