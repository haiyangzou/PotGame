package org.pot.core.cache.player.snapshot;

import java.util.function.Consumer;

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
}
