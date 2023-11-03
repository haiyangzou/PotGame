package org.pot.game.engine.player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.cache.player.PlayerCaches;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.function.Operation;
import org.pot.common.util.LogUtil;
import org.pot.core.util.SignalLight;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.PlayerProfileEntity;
import org.pot.message.protocol.login.LoginDataS2S;

import java.io.Serializable;
import java.util.function.Consumer;

@Getter
@Setter
@Slf4j
public class PlayerData implements Serializable {
    private static final long SLOW_MS = 100L;
    private volatile long uid;
    private volatile boolean loadOver = false;
    private volatile boolean loadSuccess = false;
    private volatile PlayerProfileEntity profile;

    public PlayerData(long uid) {
        this.uid = uid;
    }

    public void asyncUpdate(Operation onSuccess, Operation onFail) {
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                update(onSuccess, onFail);
            }
        });
    }

    private void update(Operation onSuccess, Operation onFail) {

    }

    public void asyncLoad(Consumer<PlayerData> onSuccess, Consumer<PlayerData> onFail) {
        String caller = ExceptionUtil.getCaller(this.getClass(), Player.class, PlayerGroup.class, PlayerManager.class);
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                SignalLight.watch("LoadPlayerData@" + uid, SLOW_MS, () -> load(caller, onSuccess, onFail));
            }
        });
    }

    public void asyncInsert(Operation onSuccess, Operation onFail) {
        String caller = ExceptionUtil.getCaller(this.getClass(), Player.class, PlayerGroup.class, PlayerManager.class);
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                SignalLight.watch("InsertPlayerData@" + uid, SLOW_MS, () -> insert(caller, onSuccess, onFail));
            }
        });
    }

    public void insert(String caller, Operation onSuccess, Operation onFail) {

    }

    public void asyncDelete(Operation onSuccess, Operation onFail) {
        String caller = ExceptionUtil.getCaller(this.getClass(), Player.class, PlayerGroup.class, PlayerManager.class);
        asyncLoad(playerData -> delete(caller, onSuccess, onFail), playerData -> log.error("Delete Player Load Fail! uid={}", uid));
    }

    public void delete(String caller, Operation onSuccess, Operation onFail) {

    }

    public void load(String caller, Consumer<PlayerData> onSuccess, Consumer<PlayerData> onFail) {

    }

    public PlayerProfileEntity onRegister(LoginDataS2S loginDataS2S) {
        if (loginDataS2S == null) {
            throw new ServiceException(LogUtil.format("LoginData Register Error uid={}", uid));
        }
        PlayerProfileEntity temp = new PlayerProfileEntity();
        temp.setUid(loginDataS2S.getGameUid());
        temp.setName(PlayerCaches.name().newUniqueName(loginDataS2S.getGameUid()));
        temp.setAccount(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getAccount()));
        temp.setDevice(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getDevice()));
        temp.setServerId(loginDataS2S.getServerId());
        temp.setUnionId(0);
        profile = temp;
        return temp;
    }

}
