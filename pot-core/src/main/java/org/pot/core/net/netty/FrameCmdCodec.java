package org.pot.core.net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.ArrayUtils;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.GzipCompressor;

import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FrameCmdCodec extends FrameCodec<FrameCmdMessage> {
    private static final int MAGIC = 0x1DEF;
    private static final Compressor compressor = new GzipCompressor();
    private final FrameCipher cipher = new FrameCipher(MAGIC);

    public FrameCmdCodec(NettyBaseEngine<FrameCmdMessage> engine) {
        super(engine);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FrameCmdMessage msg, ByteBuf out) throws Exception {
        byte[] protoData = msg.getProtoData();
        byte[] protoName = msg.getProtoName().getBytes(StandardCharsets.UTF_8);
        boolean compressed = false;
        if (protoData.length > engine.getConfig().getCompressThreshold()) {
            compressed = true;
            int beforeCompress = protoData.length;
            protoData = compressor.compress(protoData);
            int afterCompress = protoData.length;
            engine.getNetEngineStatus().addSendCompressBytes(beforeCompress, afterCompress);
        }
        byte summary = cipher.encrypt(protoData);
        int frameLength = 2 + 1 + 1 + 2 + protoName.length + 4 + protoData.length;
        ByteBuf buf = out.ensureWritable(frameLength);
        buf.writeShort(MAGIC);
        buf.writeByte(HeaderByte.toHeadByte(compressed));
        buf.writeByte(summary);
        buf.writeShort(protoName.length);
        buf.writeBytes(protoName);
        buf.writeInt(protoData.length);
        buf.writeBytes(protoData);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (detectHAProxy(ctx, in)) {
            return;
        }
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int magicField = in.readUnsignedShort();
        if (magicField != MAGIC) {
            in.clear();
            throw new ProtocolException("Illegal Magic: 0x" + Integer.toHexString(magicField).toUpperCase());
        }
        byte headerByte = in.readByte();
        byte summary = in.readByte();
        if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
        }
        int protoNameLength = in.readUnsignedShort();
        if (protoNameLength <= 0) {
            in.clear();
            throw new ProtocolException("Illegal ProtoNameLength" + protoNameLength);
        }
        int frameLength = 2 + 1 + 1 + 2 + protoNameLength;
        if (frameLength > engine.getConfig().getMaxFrameLength()) {
            in.clear();
            throw new ProtocolException("Illegal FrameLength:" + frameLength);
        }

        String protoName = in.readCharSequence(protoNameLength, StandardCharsets.UTF_8).toString();
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int protoDataLength = in.readInt();
        if (protoDataLength <= 0) {
            in.clear();
            throw new ProtocolException("Illegal data length" + protoDataLength);
        }
        frameLength = frameLength + 4 + protoDataLength;

        if (frameLength > engine.getConfig().getMaxFrameLength()) {
            in.clear();
            throw new ProtocolException("Illegal FrameLength:" + frameLength);
        }
        if (in.readableBytes() < protoDataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] protoData = new byte[protoDataLength];
        in.readBytes(protoData);
        byte[] clonedProtoData = ArrayUtils.clone(protoData);
        if (!cipher.decrypt(summary, protoData)) {
            in.clear();
        }
        if (HeaderByte.isCompressed(headerByte)) {
            int beforeCompress = protoData.length;
            protoData = compressor.compress(protoData);
            int afterCompress = protoData.length;
            engine.getNetEngineStatus().addSendCompressBytes(beforeCompress, afterCompress);
        }
        out.add(new FrameCmdMessage(protoName, protoData));
        decode = true;
    }
}
