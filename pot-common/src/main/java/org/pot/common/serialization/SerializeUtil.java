package org.pot.common.serialization;

public class SerializeUtil {
    public static final HessianSerializer hessian = new HessianSerializer();
    public static final Serializer preferred = hessian;

    public static <T> byte[] serialize(T obj) {
        try {
            return preferred.serialize(obj);
        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
