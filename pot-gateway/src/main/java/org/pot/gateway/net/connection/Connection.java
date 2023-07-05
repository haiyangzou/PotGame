package org.pot.gateway.net.connection;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.pot.gateway.net.netty.FrameMessage;
import org.pot.message.protocol.ServerNetPing;
import org.pot.message.protocol.ServerNetPong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import org.pot.common.Constants;

@Slf4j
public class Connection<M extends FrameMessage> implements IConnection<M> {

    private final Channel channel;
    private static final AtomicInteger ID_INTEGER = new AtomicInteger(0);
    private final int id = ID_INTEGER.getAndIncrement();
    private volatile String remoteHost;
    private volatile int remotePort;
    private volatile boolean immediateFlush = true;
    private final AtomicBoolean unflushed = new AtomicBoolean(false);
    private final BlockingDeque<M> recivMessageQueue = new LinkedBlockingDeque<>();
    private volatile long lastReadTime;

    public Connection(Channel channel, String remoteHost, int remotePort) {
        this.channel = channel;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void sendMessage(M message) {
        if (isClosed()) {
            return;
        }
        if (immediateFlush) {
            channel.writeAndFlush(message);
        } else {
            channel.write(message);
            unflushed.set(true);
        }
    }

    @Override
    public void flush() {
        if (isClosed()) {
            return;
        }
        if (unflushed.compareAndSet(true, false)) {
            channel.flush();
        }
    }

    @Override
    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public <T> T getAttr(AttributeKey<T> key) {
        return channel.attr(key).get();
    }

    @Override
    public <T> void setAttr(AttributeKey<T> key, T value) {
        channel.attr(key).set(value);
    }

    @Override
    public void recvMessage(M message) {
        if (message.isProtoType(ServerNetPong.class)) {
            ServerNetPong pong = message.getProto();
            long cost = System.currentTimeMillis() - pong.getPing().getTime();
            if (cost > Constants.NET_SLOW_MS) {
                log.info("net loopback time too long({}ms). conn={}", cost, this);
            }
            return;
        }
        if (message.isProtoType(ServerNetPing.class)) {
            ServerNetPing ping = message.getProto();
            ServerNetPong pong = ServerNetPong.newBuilder().setPing(ping).build();
            sendMessage(message.renew(pong));
            return;
        }
        recivMessageQueue.offer(message);
    }

    @Override
    public boolean isClosed() {
        Channel channel = getChannel();
        if (channel == null) {
            return true;
        }
        return !channel.isOpen() && !channel.isActive();
    }

    @Override
    public void disconnect(M frameMessage) {
        if (isClosed()) {
            return;
        }
        this.channel.writeAndFlush(frameMessage).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public M pollRecvMessage() {
        return this.recivMessageQueue.poll();
    }

    @Override
    public void close() {
        this.recivMessageQueue.clear();
        Channel channel = getChannel();
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public boolean isConnected() {
        Channel channel = getChannel();
        if (channel == null) {
            return false;
        }
        return channel.isOpen() && channel.isActive();
    }

    @Override
    public boolean isIdle(long connectionIdleMills) {
        return System.currentTimeMillis() - lastReadTime > connectionIdleMills;
    }

    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Override
    public int getRecvMessageQueueSize() {
        return this.recivMessageQueue.size();
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

}
