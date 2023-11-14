package org.pot.common.rpc;

import java.io.Serializable;

public class RpcMessage implements Serializable {
    public final String className;
    public final String methodName;
    public final Object[] args;

    public RpcMessage(String className, String methodName, Object[] args) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
    }
}
