package org.pot.gateway.remote;

import org.pot.common.id.UniqueIdUtil;
import org.pot.gateway.engine.GatewayEngine;

public class RemoteUserManager {
    private final RemoteUserGroup[] remoteUserGroups = new RemoteUserGroup[GatewayEngine.getInstance().getConfig().getUserGroupSize()];

    public boolean addRemoteUser(RemoteUser remoteUser) {
        int index = UniqueIdUtil.index(remoteUser.getGameUid(), remoteUserGroups.length);
        return remoteUserGroups[index].addRemoteUser(remoteUser);
    }
}
