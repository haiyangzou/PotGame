package org.pot.remote.thrift.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;
import org.pot.common.serialization.SerializeUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RPCResponse implements Serializable {
    private static final Compressor compressor = new SnappyCompressor();
    private Throwable error;
    private Object result;

    public RPCResponse(Throwable error) {
        this.error = error;
    }

    public RPCResponse(Object result) {
        this.result = result;
    }

    public static ByteBuffer toByteBuff(RPCResponse rpcRequest) throws Exception {
        byte[] bytes = SerializeUtil.serialize(rpcRequest);
        bytes = compressor.compress(bytes);
        return ByteBuffer.wrap(bytes);
    }

    public static RPCResponse parseByteBuff(ByteBuffer byteBuffer) throws Exception {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        bytes = compressor.decompress(bytes);
        return SerializeUtil.deserialize(bytes, RPCResponse.class);
    }
}
