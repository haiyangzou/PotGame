package org.pot.common.event;

public interface EventHandler<E> {
    void handlerEvent(final E event) throws Exception;
}
