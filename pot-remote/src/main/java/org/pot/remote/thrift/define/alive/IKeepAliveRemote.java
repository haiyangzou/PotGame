package org.pot.remote.thrift.define.alive;

import org.pot.remote.thrift.define.IRemote;

public interface IKeepAliveRemote {
    void ping() throws Exception;
}
