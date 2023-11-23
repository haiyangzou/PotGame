package org.pot.remote.thrift.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TTransport;

public class TTransportPool {
    private final GenericObjectPool<TTransportWrap> internalPool;

    public TTransportPool(GenericObjectPoolConfig<TTransportWrap> config, String host, int port, int timeout) {
        if (config == null) {
            config = defaultConfig();
        }
        TTransportFactory factory = new TTransportFactory(host, port, timeout, this);
        internalPool = new GenericObjectPool<>(factory, config);
    }

    public static GenericObjectPoolConfig<TTransportWrap> defaultConfig() {
        GenericObjectPoolConfig<TTransportWrap> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(2);
        config.setMaxIdle(10);
        config.setMaxTotal(20);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestOnCreate(false);
        config.setTestWhileIdle(false);
        config.setBlockWhenExhausted(true);
        return config;
    }

    public TTransport borrowObject() throws Exception {
        return internalPool.borrowObject();
    }

    public void returnObject(TTransportWrap object) {
        internalPool.returnObject(object);
    }

    public void invalidObject(TTransportWrap object) throws Exception {
        internalPool.invalidateObject(object);
    }

    public void evict() throws Exception {
        internalPool.evict();
    }

    public void prepare() throws Exception {
        internalPool.preparePool();
    }

    public void destroy() {
        internalPool.close();
    }
}
