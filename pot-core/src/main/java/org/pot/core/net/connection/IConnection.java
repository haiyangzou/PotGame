package org.pot.core.net.connection;

import org.pot.core.net.netty.FrameMessage;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public interface IConnection<M extends FrameMessage> {
    int getId();

    Channel getChannel();

    <T> T getAttr(AttributeKey<T> key);

    <T> void setAttr(AttributeKey<T> key, T value);

    String getRemoteHost();

    void setRemoteHost(String remoteHost);

    void flush();

    void setImmediateFlush(boolean immediateFlush);

    void sendMessage(M frameMessage);

    void recvMessage(M frameMessage);

    void disconnect(M frameMessage);

    M pollRecvMessage();

    void close();

    boolean isClosed();

    boolean isConnected();

    boolean isIdle(long connectionIdleMills);

    int getRecvMessageQueueSize();

    void updateLastReadTime();

}
