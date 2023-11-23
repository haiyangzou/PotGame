package org.pot.remote.thrift.client.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFastFramedTransport;

public class TTransportFactory extends BasePooledObjectFactory<TTransportWrap> {
    private final String host;
    private final int port;
    private int timeout;
    private final TTransportPool pool;

    public TTransportFactory(String host, int port, int timeout, TTransportPool pool) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.pool = pool;
    }

    @Override
    public TTransportWrap create() throws Exception {
        TTransport transport = new TSocket(host, port, timeout);
        transport = new TFastFramedTransport(transport);
        transport.open();
        return new TTransportWrap(pool, transport);
    }

    @Override
    public PooledObject<TTransportWrap> wrap(TTransportWrap tTransportWrap) {
        return new DefaultPooledObject<>(tTransportWrap);
    }

    @Override
    public void destroyObject(PooledObject<TTransportWrap> p, DestroyMode mode) throws Exception {
        p.getObject().destroy();
    }

    @Override
    public boolean validateObject(PooledObject<TTransportWrap> p) {
        return p.getObject().isOpen();
    }
}
