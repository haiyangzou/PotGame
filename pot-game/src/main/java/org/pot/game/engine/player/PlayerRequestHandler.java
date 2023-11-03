package org.pot.game.engine.player;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.engine.Handler;
import org.pot.core.engine.HandlerDefinition;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
@HandlerDefinition
public abstract class PlayerRequestHandler<RequestType extends Message> implements Handler<Player, RequestType, IErrorCode> {
    @Getter
    private final String classSimpleName;
    private final Class<RequestType> requestType;

    @SuppressWarnings("unchecked")
    public PlayerRequestHandler() {
        this.classSimpleName = this.getClass().getSimpleName();
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Actual Type Error:" + this.getClass().getName());
        }
        this.requestType = (Class<RequestType>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @Override
    public IErrorCode handle(Player objectType, RequestType requestType) throws Exception {
        return handleRequest(objectType, requestType);
    }

    @Override
    public Class<Player> getObjectType() {
        return Player.class;
    }

    @Override
    public Class<RequestType> getRequestType() {
        return requestType;
    }

    @Override
    public Class<IErrorCode> getResultType() {
        return IErrorCode.class;
    }

    @HandlerDefinition
    protected abstract IErrorCode handleRequest(Player player, RequestType request) throws Exception;

    protected boolean isAck() {
        return true;
    }

}
