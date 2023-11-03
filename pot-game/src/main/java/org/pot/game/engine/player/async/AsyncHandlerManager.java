package org.pot.game.engine.player.async;

import org.pot.common.util.ClassUtil;
import org.pot.game.engine.player.PlayerRequestHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncHandlerManager {
    private static Map<Class<? extends PlayerRequestHandler>, AbstractAsyncHandler> asyncHandlerMap = new ConcurrentHashMap<>();

    static {
        Set<Class<? extends AbstractAsyncHandler>> handlerTypeSet = ClassUtil.getSubTypeOf(
                AbstractAsyncHandler.class, AbstractAsyncHandler.class, ClassUtil::isConcrete
        );
        try {
            for (Class<? extends AbstractAsyncHandler> handlerType : handlerTypeSet) {
                AbstractAsyncHandler h = handlerType.newInstance();
                asyncHandlerMap.put(h.getHandlerType(), h);
            }
        } catch (Exception e) {
            throw new IllegalStateException("init error", e);
        }
    }

    public static AbstractAsyncHandler getAsyncHandler(PlayerRequestHandler handler) {
        return asyncHandlerMap.get(handler.getClass());
    }
}
