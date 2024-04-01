package org.pot.chat.util;

import com.eyu.kylin.magics.protocol.chat.ChatSendInfo;
import com.google.protobuf.ByteString;
import org.pot.cache.player.snapshot.PlayerSnapShot;

public class ChatUtil {
    public static ChatSendInfo toChatSendInfo(PlayerSnapShot playerSnapshot) {
        ChatSendInfo.Builder builder = ChatSendInfo.newBuilder();
        builder.setPlayerId(playerSnapshot.getUid());
        if (playerSnapshot.getName() != null)
            builder.setName(playerSnapshot.getName());
        builder.setIcon(playerSnapshot.getIcon());
        if (playerSnapshot.getAvatar() != null && (playerSnapshot.getAvatar()).length != 0)
            builder.setAvatar(ByteString.copyFrom(playerSnapshot.getAvatar()));
        builder.setFrameId(playerSnapshot.getFrameId());
        if (playerSnapshot.getUnionAlias() != null)
            builder.setUnionAlias(playerSnapshot.getUnionAlias());
        builder.setUnionPosition(playerSnapshot.getUnionRank());
        builder.setKingdomTitle(playerSnapshot.getKingdomTitle());
        builder.setArtifactId(playerSnapshot.getArtifactId());
        builder.setAbility(playerSnapshot.getPower());
        return builder.build();
    }
}

