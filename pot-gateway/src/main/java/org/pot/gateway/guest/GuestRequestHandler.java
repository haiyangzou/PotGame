package org.pot.gateway.guest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.kafka.common.protocol.types.Field.Bool;
import org.pot.core.engine.Handler;
import org.pot.core.engine.HandlerDefinition;

import com.google.protobuf.Message;

import lombok.Getter;

@HandlerDefinition
public abstract class GuestRequestHandler<RequestType extends Message> implements Handler<Guest, RequestType, Boolean> {
    @Getter
    private final String classSimpleName;
    private final Class<RequestType> requestType;

    public GuestRequestHandler(Class<RequestType> requestType) {
        this.classSimpleName = this.getClass().getSimpleName();
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Actual Type Error:" + this.getClass().getName());
        }
        this.requestType = (Class<RequestType>) ((ParameterizedType) superClass).getActualTypeArguments()[0];

    }

    @Override
    public Boolean handle(Guest objectType, RequestType requestType) throws Exception {
        return handleRequest(objectType, requestType);
    }

    @Override
    public Class<Guest> getObjectType() {
        return Guest.class;
    }

    @Override
    public Class<RequestType> getRequestType() {
        return requestType;
    }

    @Override
    public Class<Boolean> getResultType() {
        return Boolean.class;
    }

    @HandlerDefinition
    protected abstract boolean handleRequest(Guest guest, RequestType requestType) throws Exception;
}
