package org.pot.game.engine.player;

public abstract class PlayerAgentAdapter {
    volatile String SIGNAL_FLAG_REGISTER;
    volatile String SIGNAL_FLAG_LOAD;
    volatile String SIGNAL_FLAG_SAVE;
    volatile String SIGNAL_FLAG_TICK;
    volatile String SIGNAL_FLAG_ON_LOGIN;
    volatile String SIGNAL_FLAG_ON_LOGIN_SUCCESS;
    volatile String SIGNAL_FLAG_ON_LOGOUT;
    volatile String SIGNAL_FLAG_ON_NEW_DAY;
    protected final Player player;

    public PlayerAgentAdapter(Player player) {
        this.player = player;
    }
}
