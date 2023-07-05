package org.pot.gateway.guest;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.structure.ElapsedTimeMonitor;
import org.pot.core.util.LogUtil;
import org.pot.gateway.engine.PotGateway;
import org.pot.gateway.log.GatewayLogger;
import org.pot.gateway.net.netty.FrameCmdMessage;
import org.pot.message.protocol.ProtocolPrinter;
import org.pot.message.protocol.ProtocolSupport;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuestLogger {
    public static final ElapsedTimeMonitor runElapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm("GUestRequestRun",
            "ms");
    public static final ElapsedTimeMonitor waitElapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm("GUestRequestWait",
            "ms");

    static void writeResponseLog(Guest guest, Message response) {
        GatewayLogger.GUEST.logger.info("[GUEST_RESPONSE] RemoteHost={} Response={} Size={} Params={}",
                guest.getConnection() == null ? "NULL" : guest.getConnection().getRemoteHost(),
                ProtocolSupport.name(response.getClass()),
                100,
                ProtocolPrinter.printRestrictedJson(response));

    }

    static void writeErrorLog(Guest guest, long taskCreateTime, long taskStartTime, FrameCmdMessage frameCmdMessage,
            GuestRequestHandler<?> handler, IErrorCode errorCode, Throwable throwable) {
        long end = System.currentTimeMillis();
        long taskRunTime = end - taskCreateTime;
        long taskWaitTime = taskStartTime = taskCreateTime;
        long taskTotalTime = end - taskCreateTime;
        long messageWaitTime = taskStartTime - frameCmdMessage.getCreateTime();
        long messageTotalTime = end - frameCmdMessage.getCreateTime();
        Message params = frameCmdMessage.getProto();
        String error = LogUtil.format(
                "[GUEST_QUEST_ERROR] RemoteHost ={} Request={} Size={} Handler={} TaskRunTime={}ms TaskWaitTime={}ms TaskTotalTime = {}ms MessageWaitTime={}ms MessageTOtalTime={}ms ErrorCode=({}) Throwable=({}) Params={}",
                guest.getConnection() == null ? "NULL" : guest.getConnection().getRemoteHost(),
                frameCmdMessage.getProtoName(),
                frameCmdMessage.getProto().getSerializedSize(),
                handler == null ? "NULL" : handler.getClassSimpleName(),
                taskRunTime, taskWaitTime, taskTotalTime, messageWaitTime, messageTotalTime, errorCode,
                ProtocolPrinter.printJson(params));
        if (throwable == null) {
            log.error(error);
            GatewayLogger.GUEST.logger.error(error);
        } else {
            log.error(error, throwable);
            GatewayLogger.GUEST.logger.error(error, throwable);
        }
    }

    static void writeRequestLog(Guest guest, long taskCreateTime, long taskStartTime, FrameCmdMessage frameCmdMessage,
            GuestRequestHandler<?> handler) {
        long end = System.currentTimeMillis();
        long taskRunTime = end - taskCreateTime;
        long taskWaitTime = taskStartTime = taskCreateTime;
        long taskTotalTime = end - taskCreateTime;
        long messageWaitTime = taskStartTime - frameCmdMessage.getCreateTime();
        long messageTotalTime = end - frameCmdMessage.getCreateTime();
        Message params = frameCmdMessage.getProto();
        boolean runSlow = taskRunTime > PotGateway.getInstance().getConfig().getGuestReqExecSlowMillis();
        boolean waitSlow = messageWaitTime > PotGateway.getInstance().getConfig().getGuestReqWaitSlowMillis();
        if (runSlow) {
            runElapsedTimeMonitor.recordElapsedTime(frameCmdMessage.getProtoName(), taskRunTime);
        }
        if (waitSlow) {
            waitElapsedTimeMonitor.recordElapsedTime(frameCmdMessage.getProtoName(), messageTotalTime);
        }
        String tag = (runSlow || waitSlow) ? "[GUEST_REQUEST_SLOW]" : "[GUEST_REQUEST]";
        String info = LogUtil.format(
                "{} RemoteHost ={} Request={} Size={} Handler={} TaskRunTime={}ms TaskWaitTime={}ms TaskTotalTime = {}ms MessageWaitTime={}ms MessageTOtalTime={}ms ErrorCode=({}) Throwable=({}) Params={}",
                tag, guest.getConnection() == null ? "NULL" : guest.getConnection().getRemoteHost(),
                frameCmdMessage.getProtoName(),
                frameCmdMessage.getProto().getSerializedSize(),
                handler == null ? "NULL" : handler.getClassSimpleName(),
                taskRunTime, taskWaitTime, taskTotalTime, messageWaitTime, messageTotalTime,
                (runSlow || waitSlow) ? ProtocolPrinter.printJson(params)
                        : ProtocolPrinter.printRestrictedJson(params));
        if (runSlow || waitSlow)
            log.info(info);
        GatewayLogger.GUEST.logger.info(info);
    }
}
