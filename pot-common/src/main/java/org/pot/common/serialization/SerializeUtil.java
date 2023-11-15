package org.pot.common.serialization;


import java.io.IOException;

public class SerializeUtil {
    public static final KryoSerializer kryo = new KryoSerializer();
    public static final HessianSerializer hessian = new HessianSerializer();
    public static final Serializer preferred = hessian;

    public static <T> byte[] serialize(T obj) {
        try {
            return preferred.serialize(obj);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clz) {
        try {
            return preferred.deserialize(data, clz);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
