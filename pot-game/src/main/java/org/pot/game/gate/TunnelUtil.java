package org.pot.game.gate;

import com.google.protobuf.ByteString;
import org.pot.common.compressor.Lz4Compressor;
import org.pot.common.serialization.SerializeUtil;
import org.pot.common.util.JsonUtil;
import org.pot.game.engine.player.PlayerData;

import java.nio.charset.StandardCharsets;

public class TunnelUtil {
    private static final Lz4Compressor compressor = new Lz4Compressor();

    public static ByteString savePlayerData(PlayerData playerData) {
        byte[] bytes = SerializeUtil.serialize(playerData);
        return ByteString.copyFrom(compressor.compress(bytes));
    }

    public static ByteString saveVisaData(TunnelVisaData visaData) {
        String json = JsonUtil.toJson(visaData);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return ByteString.copyFrom(compressor.compress(bytes));
    }

    public static PlayerData loadPlayerData(ByteString bytesString) {
        byte[] bytes = compressor.decompress(bytesString.toByteArray());
        return SerializeUtil.deserialize(bytes, PlayerData.class);
    }

    public static TunnelVisaData loadVisaData(ByteString bytesString) {
        byte[] bytes = compressor.decompress(bytesString.toByteArray());
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JsonUtil.parseJson(json, TunnelVisaData.class);
    }


}
