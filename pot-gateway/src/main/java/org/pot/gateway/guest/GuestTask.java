package org.pot.gateway.guest;

import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.gateway.net.netty.FrameCmdMessage;
import org.pot.message.protocol.ProtocolSupport;

import com.google.protobuf.Message;

import lombok.Getter;

public class GuestTask implements Runnable {
    @Getter
    private final long createTime = System.currentTimeMillis();
    private final Guest guest;

    public GuestTask(Guest guest) {
        this.guest = guest;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        FrameCmdMessage frameCmdMessage;
        Class<? extends Message> protoType = null;
        GuestRequestHandler<?> guestRequestHandler = null;
        while ((frameCmdMessage = guest.getConnection().pollRecvMessage()) != null) {
            protoType = frameCmdMessage.getProtoType();
            guestRequestHandler = GuestRequestHandlerSupport.getHandler(protoType);
            if (guestRequestHandler != null) {
                break;
            }
        }
        if (frameCmdMessage == null) {
            GuestWaitingRoom.getInstance().addWaitingGuest(this.guest);
            return;
        }
        Throwable throwable = null;
        IErrorCode errorCode = null;
        try {

        } catch (ServiceException e) {
            throwable = e;
            errorCode = CommonErrorCode.INVALID_REQUEST;
        } catch (Throwable ex) {
            throwable = ex;
            errorCode = CommonErrorCode.INVALID_REQUEST;
        }
        guest.sendMessage(ProtocolSupport.buildProtoAckMsg(protoType));
        if (errorCode != null) {
            GuestLogger.writeErrorLog(guest, createTime, startTime, frameCmdMessage, guestRequestHandler, errorCode,
                    throwable);
            guest.disconnect(ProtocolSupport.buildProtoErrorMsg(protoType, errorCode));
        }
        GuestLogger.writeRequestLog(guest, createTime, startTime, frameCmdMessage, guestRequestHandler);
    }

}
