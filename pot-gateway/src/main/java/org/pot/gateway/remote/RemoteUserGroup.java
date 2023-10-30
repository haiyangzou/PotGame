package org.pot.gateway.remote;


import lombok.Getter;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class RemoteUserGroup extends Thread {
    @Getter
    private final int index;

    private final BlockingQueue<RemoteUser> waitingUsers = new LinkedBlockingDeque<>();
    private final Map<Long, RemoteUser> remoteUsers = new ConcurrentHashMap<>();

    private volatile boolean shutdown = false;
    private volatile boolean closed = false;

    public RemoteUserGroup(int index) {
        this.index = index;
        String name = this.getClass().getSimpleName() + "-" + this.index;

    }

    boolean addRemoteUser(RemoteUser remoteUser) {
        return waitingUsers.offer(remoteUser);
    }

    RemoteUser getRemoteUser(long gameUid) {
        return remoteUsers.get(gameUid);
    }

    int getRemoteUserCount() {
        return remoteUsers.size();
    }

    int getWaitingUserCount() {
        return waitingUsers.size();
    }
}
