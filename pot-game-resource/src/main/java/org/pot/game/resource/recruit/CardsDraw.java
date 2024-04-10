package org.pot.game.resource.recruit;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;
import org.pot.game.resource.common.Drop;
import org.pot.game.resource.common.Reward;

@Getter
public class CardsDraw extends InitJsonConfigSpec {
    private int poolId;


    private long score;
    private int drawCount;

    private int type;

    private Reward cost;

    private int openId;

    private Drop drop;

    private int firstDraw;

    private Drop[] firstDrop;
}
