package org.pot.game.engine.player;

import com.google.protobuf.Message;
import org.pot.common.util.ClassUtil;
import org.pot.core.engine.HandlerFactory;
import org.pot.message.protocol.ProtocolSupport;

import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class PlayerRequestHandlerSupport {
    private static final HandlerFactory<String, PlayerRequestHandler> factory = new HandlerFactory<>();

    private static void addHandler(Class<? extends Message> proto, Class<? extends PlayerRequestHandler> handler) {
        factory.addHandler(ProtocolSupport.name(proto), handler);
    }

    public static PlayerRequestHandler getHandler(Class<? extends Message> proto) {
        return factory.getHandler(ProtocolSupport.name(proto));
    }

    static {
        Set<Class<? extends PlayerRequestHandler>> handlerTypeSet = ClassUtil.getSubTypeOf(PlayerRequestHandler.class,
                PlayerRequestHandler.class, ClassUtil::isConcrete);
        try {
            for (Class<? extends PlayerRequestHandler> handlerType : handlerTypeSet) {
                PlayerRequestHandler<? extends Message> h = handlerType.newInstance();
                addHandler(h.getRequestType(), handlerType);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("init error", ex);
        }
    }
}