package org.pot.common.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public interface Serializer {
    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

    <T> T deserialize(ByteBuffer data, Class<T> clz) throws IOException;

    <T> T deserialize(byte[] data, int offset, int size, Class<T> clz) throws IOException;

    <T> String writeObjectToString(T obj) throws IOException, DataFormatException;

    <T> T readObjectFromString(String str, Class<T> clz) throws IOException, DataFormatException;

}
