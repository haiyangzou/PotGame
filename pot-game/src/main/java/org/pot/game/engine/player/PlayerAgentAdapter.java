package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.common.util.StringUtil;
import org.pot.message.protocol.login.LoginRespS2C;

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
    @Getter
    protected final String classSimpleName;

    public PlayerAgentAdapter(Player player) {
        this.player = player;
        classSimpleName = this.getClass().getSimpleName().intern();
    }

    void initSignalFlags() {
        this.SIGNAL_FLAG_REGISTER = StringUtil.format("Player@{}_Register_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_LOAD = StringUtil.format("Player@{}_Load_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_ON_LOGIN = StringUtil.format("Player@{}_onLogin_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_ON_LOGIN_SUCCESS = StringUtil.format("Player@{}_onLoginSuccess_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_ON_LOGOUT = StringUtil.format("Player@{}_onLogout_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_SAVE = StringUtil.format("Player@{}_Save_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_TICK = StringUtil.format("Player@{}_Tick_{}", player.getUid(), classSimpleName);
        this.SIGNAL_FLAG_ON_NEW_DAY = StringUtil.format("Player@{}_onNewDay_{}", player.getUid(), classSimpleName);
    }

    protected void saveData(PlayerData playerData) {

    }

    protected void tick() {

    }

    protected void initData(PlayerData playerData) {

    }

    protected void loadData(PlayerData playerData) {

    }

    protected void onLogin(LoginRespS2C.Builder loginDataBuilder) {

    }

    protected void onLoginSuccess() {

    }

    protected void onLogout() {

    }

    public long calculatePower() {
        return 0;
    }

}
