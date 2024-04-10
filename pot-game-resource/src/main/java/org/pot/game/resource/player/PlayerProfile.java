package org.pot.game.resource.player;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

@Getter
public class PlayerProfile extends InitJsonConfigSpec {
    private int type;

    private int initWear;

    private int unlockType;

    private int unlockValue;

    private int timeType;

    private String itemType;

    private int buffIds1;

    private int buffIds2;

    private int buffIds3;

    private int buffIds4;
}
