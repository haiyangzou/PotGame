package org.pot.common.serialization;

import java.io.IOException;

public interface Serializer {
    byte[] serialize(Object obj) throws IOException;
}
