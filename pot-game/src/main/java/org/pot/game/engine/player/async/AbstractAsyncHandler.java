package org.pot.game.engine.player.async;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.util.LogUtil;
import org.pot.common.util.NumberUtil;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.ProtocolPrinter;

import java.lang.reflect.ParameterizedType;

@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractAsyncHandler<T extends PlayerRequestHandler, RequestType extends Message> {
    protected Class<? extends PlayerRequestHandler> getHandlerType() {
        return (Class<? extends PlayerRequestHandler>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void async(T handler, Player player, FramePlayerMessage framePlayerMessage) {
        long start = System.currentTimeMillis();
        GameEngine.getInstance().getAsyncExecutor().execute(() -> {
            long runTime = System.currentTimeMillis();
            try {
                IErrorCode beforeErrorCode = beforeProcess(player, framePlayerMessage.getProto());
                if (beforeErrorCode != null) {
                    if (!(beforeErrorCode instanceof AsyncErrorCode)) {
                        player.errorProcess(beforeErrorCode, null, framePlayerMessage, handler, start);
                    }
                    return;
                }
            } catch (Exception e) {

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
