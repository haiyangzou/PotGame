package org.pot.remote.thrift.client.pool;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TConfiguration;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

@Slf4j
public class TTransportWrap extends TTransport {
    private final TTransportPool pool;
    private TTransport delegate;

    public TTransportWrap(TTransportPool pool, TTransport delegate) {
        this.pool = pool;
        this.delegate = delegate;
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public void open() throws TTransportException {
        try {

            delegate.open();
        } catch (Exception e) {
            invalid();
            throw e;
        }
    }

    @Override
    public void close() {
        if (delegate == null) {
            return;
        }
        if (pool == null) {
            delegate.close();
        } else {
            pool.returnObject(this);
        }
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws TTransportException {
        try {
            return delegate.read(bytes, i, i1);
        } catch (Exception e) {
            invalid();
            throw e;
        }
    }

    @Override
    public int readAll(byte[] buf, int off, int len) throws TTransportException {
        try {
            return delegate.readAll(buf, off, len);
        } catch (Exception e) {
            invalid();
            throw e;
        }
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws TTransportException {
        try {
            delegate.write(bytes, i, i1);
        } catch (Exception e) {
            invalid();
            throw e;
        }
    }

    @Override
    public void flush() throws TTransportException {
        try {
            delegate.flush();
        } catch (Exception e) {
            invalid();
            throw e;
        }
    }

    @Override
    public byte[] getBuffer() {
        return delegate.getBuffer();
    }

    @Override
    public int getBufferPosition() {
        return delegate.getBufferPosition();
    }

    @Override
    public int getBytesRemainingInBuffer() {
        return delegate.getBytesRemainingInBuffer();
    }

    @Override
    public void consumeBuffer(int len) {
        delegate.consumeBuffer(len);
    }

    @Override
    public TConfiguration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public void updateKnownMessageSize(long l) throws TTransportException {
        delegate.updateKnownMessageSize(l);
    }

    @Override
    public void checkReadBytesAvailable(long l) throws TTransportException {
        delegate.checkReadBytesAvailable(l);
    }

    private void invalid() {
        if (pool != null) {
            try {
                pool.invalidObject(this);
            } catch (Exception e) {
                log.error("invalid TTWap occ", e);
            }
        }
    }

    void destroy() {
        if (delegate != null) {
            delegate.close();
            delegate = null;
        }
    }
}
