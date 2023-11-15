package org.pot.common.serialization;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.pot.common.binary.Base64Util;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class HessianSerializer implements Serializer {
    private final Compressor compressor = new SnappyCompressor();

    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(byteArrayOutputStream);
        out.writeObject(obj);
        out.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return deserialize(data, 0, data.length, clz);
    }

    @Override
    public <T> T deserialize(ByteBuffer data, Class<T> clz) throws IOException {
        if (data == null || data.remaining() == 0) return null;
        return deserialize(data.array(), data.position(), data.remaining(), clz);
    }

    @Override
    public <T> T deserialize(byte[] data, int offset, int size, Class<T> clz) throws IOException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data, offset, size));
        return (T) input.readObject(clz);
    }

    @Override
    public <T> String writeObjectToString(T obj) throws IOException, DataFormatException {
        return Base64Util.encode(compressor.compress(serialize(obj)));
    }

    @Override
    public <T> T readObjectFromString(String str, Class<T> clz) throws IOException, DataFormatException {
        return deserialize(compressor.decompress(Base64Util.decode(str)), clz);
    }
}
