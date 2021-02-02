package org.pot.core.service;

import org.springframework.stereotype.Service;

/**
 * http 通信
 *
 */
@Service
public abstract class HttpService implements INettyService<String> {
    public HttpService() {
    }
    @Override
    public boolean sendMsg(Object msg) {
        throw new UnsupportedOperationException();
    }
}
