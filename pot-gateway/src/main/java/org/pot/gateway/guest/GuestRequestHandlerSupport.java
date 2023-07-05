package org.pot.gateway.guest;

import java.util.Set;

import org.pot.common.util.ClassUtil;
import org.pot.core.engine.HandlerFactory;
import org.pot.message.protocol.ProtocolSupport;

import com.google.protobuf.Message;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class GuestRequestHandlerSupport {
    private static final HandlerFactory<String, GuestRequestHandler> factory = new HandlerFactory<>();

    private static void addHandler(Class<? extends Message> proto, Class<? extends GuestRequestHandler> handler) {
        factory.addHandler(ProtocolSupport.name(proto), handler);
    }

    public static GuestRequestHandler getHandler(Class<? extends Message> proto) {
        return factory.getHandler(ProtocolSupport.name(proto));
    }

    static {
        Set<Class<? extends GuestRequestHandler>> handlerTypeSet = ClassUtil.getSubTypeOf(GuestRequestHandler.class,
                GuestRequestHandler.class, ClassUtil::isConcrete);
        try {
            for (Class<? extends GuestRequestHandler> handlerType : handlerTypeSet) {
                GuestRequestHandler<? extends Message> h = handlerType.newInstance();
                addHandler(h.getRequestType(), handlerType);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("init error", ex);
        }
    }
}