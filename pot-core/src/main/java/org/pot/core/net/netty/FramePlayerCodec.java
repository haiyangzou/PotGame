package org.pot.core.net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.SnappyCompressor;

import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FramePlayerCodec extends FrameCodec<FramePlayerMessage> {
    private static final Compressor compressor = new SnappyCompressor();

    public FramePlayerCodec(NettyBaseEngine<FramePlayerMessage> engine) {
        super(engine);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FramePlayerMessage msg, ByteBuf out) throws Exception {
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
        int frameLength = 4 + 1 + 8 + 2 + protoName.length + 4 + protoData.length;
        ByteBuf buf = out.ensureWritable(frameLength);
        buf.writeInt(frameLength);
        buf.writeByte(HeaderByte.toHeadByte(compressed));
        buf.writeLong(msg.getPlayerId());
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
        int frameLength = in.readInt();
        if (frameLength > engine.getConfig().getMaxFrameLength()) {
            in.clear();
            throw new ProtocolException("Illegal FrameLength:" + frameLength);
        }
        if ((in.readableBytes() + 4) < frameLength) {
            in.resetReaderIndex();
            return;
        }
        byte headerByte = in.readByte();
        long playerId = in.readLong();
        int protoNameLength = in.readUnsignedShort();
        if (protoNameLength <= 0) {
            in.clear();
            throw new ProtocolException("Illegal Name" + protoNameLength);
        }
        String protoName = in.readCharSequence(protoNameLength, StandardCharsets.UTF_8).toString();
        int protoDataLength = in.readInt();
        if (protoDataLength <= 0) {
            in.clear();
            throw new ProtocolException("Illegal data length" + protoDataLength);
        }

        byte[] protoData = new byte[protoDataLength];
        in.readBytes(protoData);
        if (HeaderByte.isCompressed(headerByte)) {
            int beforeCompress = protoData.length;
            protoData = compressor.compress(protoData);
            int afterCompress = protoData.length;
            engine.getNetEngineStatus().addSendCompressBytes(beforeCompress, afterCompress);
        }
        out.add(new FramePlayerMessage(playerId, protoName, protoData));
        decode = true;
    }
}
