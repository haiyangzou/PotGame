package org.pot.game.engine.player.async;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.util.LogUtil;
import org.pot.common.util.NumberUtil;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.ProtocolPrinter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j

public abstract class AbstractAsyncHandler<HandlerType extends PlayerRequestHandler<RequestType>, RequestType extends Message> {
    @Getter
    private final String classSimpleName;
    @Getter
    private final Class<HandlerType> handlerType;
    @Getter
    private final Class<RequestType> requestType;

    @SuppressWarnings("unchecked")
    public AbstractAsyncHandler() {
        this.classSimpleName = this.getClass().getSimpleName();
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Actual Type Error:" + this.getClass().getName());
        }
        this.handlerType = (Class<HandlerType>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        this.requestType = (Class<RequestType>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
    }

    public void async(HandlerType handler, Player player, FramePlayerMessage framePlayerMessage) {
        long start = System.currentTimeMillis();
        GameEngine.getInstance().getAsyncExecutor().execute(() -> {
            long runTime = System.currentTimeMillis();
            Throwable throwable = null;
            IErrorCode beforeErrorCode = null;
            try {
                beforeErrorCode = beforeProcess(player, framePlayerMessage.getProto());
            } catch (ServiceException e) {
                throwable = e;
                beforeErrorCode = e.getErrorCode();
            } catch (Throwable ex) {
                throwable = ex;
                beforeErrorCode = CommonErrorCode.UNKNOWN_ERROR;
            }
            if (throwable != null) {
                log.error("Async handler beforeProcess");
            }
            if (beforeErrorCode != null) {
                if (!(beforeErrorCode instanceof AsyncErrorCode)) {
                    player.errorProcess(beforeErrorCode, null, framePlayerMessage, handler, start);
                }
                return;
            }

            player.submit(() -> {
                player.onHandle(start, handler, framePlayerMessage);
                long handleEndTime = System.currentTimeMillis();
                GameEngine.getInstance().getAsyncExecutor().execute(() -> {
                    long runAfterTIme = System.currentTimeMillis();
                    this.afterProcess(player, framePlayerMessage.getProto());
                    slowLog("[P_ASYNC_REQ_AFT]", handleEndTime, runAfterTIme, player, framePlayerMessage, handler);
                });
            });
        });
    }

    private void slowLog(String logType, long startTime, long runTime, Player player, FramePlayerMessage framePlayerMessage, PlayerRequestHandler handler) {
        long waiTime = runTime - startTime;
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > Constants.NET_SLOW_MS) {
            String info = LogUtil.format("{} Sid = Pid={} Proto={} Size={} Handler={} Run={} Wait={} Total={} Params={}",
                    logType,
                    GameServerInfo.getServerId(),
                    player.getUid(),
                    framePlayerMessage.getProtoName(),
                    NumberUtil.byteCountToDisplaySize(framePlayerMessage.getProto().getSerializedSize()),
                    handler == null ? "NULL" : handler.getClassSimpleName(),
                    (elapsed - waiTime), waiTime, elapsed, ProtocolPrinter.printJson((Message) framePlayerMessage));
            log.warn(info);
        }
    }

    protected IErrorCode afterProcess(Player player, RequestType request) {
        return null;
    }

    protected IErrorCode beforeProcess(Player player, RequestType request) {
        return null;
    }

    protected enum AsyncErrorCode implements IErrorCode {
        SKIP_HANDLER_AND_AFTER(-1000),
        ;
        public final int errorCode;

        AsyncErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public int getErrorCode() {
            return errorCode;
        }

        @Override
        public String getErrorName() {
            return name();
        }
    }
}
