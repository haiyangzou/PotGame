package org.pot.common.event;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class EventBus {
    private final Map<Class<?>, CopyOnWriteArrayList<EventHandler>> subscribers = new ConcurrentHashMap<>();
    private final BlockingQueue<Object> events = new LinkedBlockingDeque<>();
    private volatile long threadId = -1;

    public void setThreadId() {
        long busThreadId = threadId;
        long currentThreadId = Thread.currentThread().getId();
        if (busThreadId > 0 && busThreadId != currentThreadId) {
            log.error("current thread({}) not equals bus thread({})", currentThreadId, busThreadId);
        }
        threadId = currentThreadId;
    }

    public void handleAsyncEvent() {
        long busThreadId = threadId;
        long currentThreadId = Thread.currentThread().getId();
        if (busThreadId > 0 && busThreadId != currentThreadId) {
            log.error("current thread({}) not equals bus thread({})", currentThreadId, busThreadId);
        }
        Object event;
        while ((event = events.poll()) != null) {
            post0(event);
        }
    }

    public void post(final Object event) {
        long busThreadId = threadId;
        long currentThreadId = Thread.currentThread().getId();
        if (busThreadId > 0) {
            log.error("current thread({}) not equals bus thread({})", currentThreadId, busThreadId);
        }
        post0(event);
    }
    @SuppressWarnings("unchecked")
    private void post0(final Object event) {
        CopyOnWriteArrayList<EventHandler> eventHandlers = subscribers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler eventHandler : eventHandlers) {
                try {
                    eventHandler.handlerEvent(event);
                } catch (Exception e) {
                    log.error("Handle Event {} Error", event, e);
                }
            }
        }
    }

    public final <E, H extends EventHandler<E>> void register(final Class<E> eventKind, final H eventHandler) {
        if (eventKind != null && eventHandler != null) {
            CopyOnWriteArrayList<EventHandler> eventHandlers = subscribers.computeIfAbsent(eventKind, key -> new CopyOnWriteArrayList<>());
            boolean success = eventHandlers.addIfAbsent(eventHandler);
            if (!success) {
                log.error("Add Event{} handler {} Failed", eventKind.getSimpleName(), eventHandler.getClass().getName());
            }
        }
    }

    public final <E, H extends EventHandler<E>> void unregister(final Class<E> eventKind, final H eventHandler) {
        CopyOnWriteArrayList<EventHandler> eventHandlers = subscribers.get(eventKind);
        if (eventHandlers != null) {
            eventHandlers.remove(eventHandler);
        }
    }

    public final void clear() {
        subscribers.clear();
    }

    public void register(Object obj) {
        unregister(obj);
        ImmutableList<Method> subscriberMethods = getSubScribeMethods(obj.getClass());
        for (Method subscriberMethod : subscriberMethods) {
            Class<?> eventKind = subscriberMethod.getParameterTypes()[0];
            ProxyEventHandler handler = null;
            try {
                handler = ProxyEventHandler.proxyEventHandler(obj, subscriberMethod, eventKind);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (handler != null) {
                register(eventKind, handler);
            }
        }
    }

    public void unregister(Object obj) {
        ImmutableList<Method> subscriberMethods = getSubScribeMethods(obj.getClass());
        for (Method subscriberMethod : subscriberMethods) {
            Class<?> eventKind = subscriberMethod.getParameterTypes()[0];
            CopyOnWriteArrayList<EventHandler> eventHandlers = subscribers.get(eventKind);
            if (eventHandlers != null) {
                eventHandlers.removeIf(h -> h instanceof ProxyEventHandler && Objects.equals(obj, ((ProxyEventHandler) h).getProxy()));
            }
        }
    }

    private static final Map<Class<?>, ImmutableList<Method>> subscriberMethodCache = new ConcurrentHashMap<>();

    private static ImmutableList<Method> getSubScribeMethods(Class<?> clazz) {
        return subscriberMethodCache.computeIfAbsent(clazz, EventBus::getSubScribeMethods);
    }
}
