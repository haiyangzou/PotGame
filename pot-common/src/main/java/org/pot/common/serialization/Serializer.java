package org.pot.common.serialization;

import java.io.IOException;

public interface Serializer {
    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

}
