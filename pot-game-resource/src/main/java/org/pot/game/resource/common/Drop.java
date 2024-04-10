package org.pot.game.resource.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Drop {
    private int scene;

    private int id;

    private int count;


    public Drop(int scene, int id, int count) {
        this.scene = scene;
        this.id = id;
        this.count = count;
    }
    public Drop(int id, int count) {
        this.id = id;
        this.count = count;
    }
}
