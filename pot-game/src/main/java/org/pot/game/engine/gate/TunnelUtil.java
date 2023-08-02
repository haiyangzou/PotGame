package org.pot.game.engine.gate;

import com.google.protobuf.ByteString;
import org.pot.common.compressor.Lz4Compressor;
import org.pot.common.serialization.SerializeUtil;
import org.pot.common.util.JsonUtils;
import org.pot.game.engine.player.PlayerData;

import java.nio.charset.StandardCharsets;

public class TunnelUtil {
    private static final Lz4Compressor compressor = new Lz4Compressor();

    public static ByteString savePlayerData(PlayerData playerData) {
        byte[] bytes = SerializeUtil.serialize(playerData);
        return ByteString.copyFrom(compressor.compress(bytes));
    }

    public static ByteString saveVisaData(TunnelVisaData visaData) {
        String json = JsonUtils.toJson(visaData);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return ByteString.copyFrom(compressor.compress(bytes));
    }
}
