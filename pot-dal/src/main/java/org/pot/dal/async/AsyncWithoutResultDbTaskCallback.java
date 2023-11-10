package org.pot.dal.async;

import lombok.Setter;
import org.pot.common.function.Operation;

public class AsyncWithoutResultDbTaskCallback implements IAsyncDbTaskCallBack {
    @Setter
    private volatile AsyncWithoutResultDbTask<?> asyncWithoutResultDbTask;
    private final Operation onSuccess;
    private final Operation onFail;

    public AsyncWithoutResultDbTaskCallback(Operation onSuccess, Operation onFail) {
        this.onSuccess = onSuccess;
        this.onFail = onFail;
    }

    @Override
    public void callback() {
        Throwable throwable = asyncWithoutResultDbTask.getThrowable();
        if (throwable != null) {
            if (onFail != null) {
                onFail.operate();
            }
        } else {
            if (onSuccess != null) {
                onSuccess.operate();
            }
        }
    }
}
