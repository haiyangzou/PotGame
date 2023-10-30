package org.pot.common.http;

import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.function.ExFunction;

import java.util.TreeMap;
import java.util.function.Function;

public class HttpSignature {
    public static String generate(TreeMap<String, String> parameters,
                                  ExFunction<String, String> signMethod,
                                  Function<TreeMap<String, String>, String> sourceMethod) {
        try {
            return signMethod.apply(sourceMethod.apply(parameters));
        } catch (Throwable ex) {
            throw new ServiceException(CommonErrorCode.INVALID_REQUEST, ex);
        }
    }

}
