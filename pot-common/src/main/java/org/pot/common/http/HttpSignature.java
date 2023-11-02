package org.pot.common.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.function.ExFunction;

import java.util.TreeMap;
import java.util.function.Function;

@Slf4j
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

    public static boolean validate(String signature, TreeMap<String, String> withoutSignParameters,
                                   ExFunction<String, String> signMethod,
                                   Function<TreeMap<String, String>, String> sourceMethod) {
        try {
            if (StringUtils.isBlank(signature)) return false;
            return signature.equals(generate(withoutSignParameters, signMethod, sourceMethod));
        } catch (Throwable ex) {
            log.error("validate signature error");
            return false;
        }
    }


}
