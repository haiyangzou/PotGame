package org.pot.cache.player.snapshot;

import java.util.function.Consumer;

import org.pot.core.util.JsonUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class PlayerSnapShot {
    private String account;
    private long regtime;
    private long uid;
    private transient volatile Consumer<Long> flushMarker;

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
        return JsonUtils.toT(data, PlayerSnapShot.class);
    }
    public String toJson(){
        return JsonUtils.toJSON(this);
    }
}
