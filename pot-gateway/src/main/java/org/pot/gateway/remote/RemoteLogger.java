package org.pot.gateway.remote;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.NumberUtil;
import org.pot.core.net.netty.FrameCmdMessage;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.gateway.engine.GatewayEngine;

@Slf4j
public class RemoteLogger {
    static void writeRequestLog(RemoteUser remoteUser, FrameCmdMessage frameCmdMessage, long startTime) {
        if (GatewayEngine.getInstance().getConfig().isGatewayRequestLogDisable())
            return;
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        long waitTime = startTime - frameCmdMessage.getCreateTime();
        long totalTime = endTime - frameCmdMessage.getCreateTime();
        log.info("[GATEWAY_REQUEST] Server={} Player={} Request={} Size={} RunTime={}ms WaitTime={}ms TotalTime={}ms", remoteUser.getServerId(),
                remoteUser.getGameUid(), frameCmdMessage.getProtoName(),
                NumberUtil.byteCountToDisplaySize((frameCmdMessage.getProtoData()).length),
                runTime, waitTime, totalTime);
    }

    static void writeResponseLog(RemoteServer remoteServer, RemoteUser remoteUser, FramePlayerMessage framePlayerMessage, long startTime) {
        if (GatewayEngine.getInstance().getConfig().isGatewayResponseLogDisable())
            return;
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        long waitTime = startTime - framePlayerMessage.getCreateTime();
        long totalTime = endTime - framePlayerMessage.getCreateTime();
        log.info("[GATEWAY_RESPONSE] Server={} Player={} Response={} Size={} Online={} RunTime={}ms WaitTime={}ms TotalTime={}ms", remoteServer.getServerId(),
                framePlayerMessage.getPlayerId(), framePlayerMessage.getProtoName(),
                NumberUtil.byteCountToDisplaySize((framePlayerMessage.getProtoData()).length),
                remoteUser != null && remoteUser.getConnection() != null && remoteUser.getConnection().isConnected(),
                runTime, waitTime, totalTime);
    }
}
