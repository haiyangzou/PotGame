package org.pot.gateway.remote;

import org.pot.common.Constants;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.gateway.engine.GatewayEngine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RemoteUserManager {
    private final RemoteUserGroup[] remoteUserGroups = new RemoteUserGroup[GatewayEngine.getInstance().getConfig().getUserGroupSize()];

    public RemoteUserManager() {
        for (int i = 0; i < remoteUserGroups.length; i++) {
            remoteUserGroups[i] = new RemoteUserGroup(i);
        }
    }

    public void close() {
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> {
            Arrays.stream(remoteUserGroups).forEach(RemoteUserGroup::close);
            return Arrays.stream(remoteUserGroups).allMatch(RemoteUserGroup::isClosed);
        });
    }


    public boolean addRemoteUser(RemoteUser remoteUser) {
        int index = UniqueIdUtil.index(remoteUser.getGameUid(), remoteUserGroups.length);
        return remoteUserGroups[index].addRemoteUser(remoteUser);
    }
}
