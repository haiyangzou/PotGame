package org.pot.cache.player.snapshot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


@Slf4j
@Getter
@Setter
public class PlayerSnapShot {
    private transient volatile Consumer<Long> flushMarker;
    private String name;

    private int level;

    private int castleLevel;

    private long uid;

    private int serverId;

    private int pointId;

    private boolean online;

    private long lastOnlineTime;

    private byte[] avatar;

    private int frameId;

    private int icon;

    private int kingdomTitle;

    private int artifactId;

    private String language;

    private int unionId;

    private String unionName;

    private String unionAlias;

    private int unionRank;

    private long power;

    private long killCount;

    private Map<String, String> varMap = new HashMap<>();

    public PlayerSnapShot markChanged() {
        Consumer<Long> temp = flushMarker;
        if (temp != null) {
            temp.accept(uid);
        } else {
            log.warn("flushMarker is null ,uid={}", uid);
        }
        return this;
    }

    public static PlayerSnapShot fromJson(String data) {
        return JsonUtil.parseJson(data, PlayerSnapShot.class);
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }
}
