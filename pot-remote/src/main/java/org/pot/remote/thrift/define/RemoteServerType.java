package org.pot.remote.thrift.define;

import org.pot.common.communication.server.ServerType;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteServerType {
    ServerType[] value();
}
