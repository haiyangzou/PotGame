package org.pot.dal.dao.param;

import java.util.Iterator;

public class EntityBatchParamSetter<E> extends AbstractEntityBatchParamSetter<E> {
    private final Iterable<E> iterable;
    private Iterator<E> iterator;

    public EntityBatchParamSetter(PropertyParamSetter<E> propertyParamSetter, Iterable<E> iterable) {
        super(propertyParamSetter);
        this.iterable = iterable;
        this.iterator = iterable.iterator();
    }

    @Override
    protected E next() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public void reset() {
        this.iterator = iterable.iterator();
    }
}
