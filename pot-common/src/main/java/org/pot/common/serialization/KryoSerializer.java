package org.pot.common.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class KryoSerializer implements Serializer {
    public static final KryoSerializer instance = new KryoSerializer();

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return null;
    }

    @Override
    public <T> T deserialize(ByteBuffer data, Class<T> clz) throws IOException {
        return null;
    }

    @Override
    public <T> T deserialize(byte[] data, int offset, int size, Class<T> clz) throws IOException {
        return null;
    }

    @Override
    public <T> String writeObjectToString(T obj) throws IOException, DataFormatException {
        return null;
    }

    @Override
    public <T> T readObjectFromString(String str, Class<T> clz) throws IOException, DataFormatException {
        return null;
    }
}
