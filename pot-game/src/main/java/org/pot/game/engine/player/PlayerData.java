package org.pot.game.engine.player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.cache.player.PlayerCaches;
import org.pot.common.binary.Base64Util;
import org.pot.common.compressor.Compressor;
import org.pot.common.compressor.GzipCompressor;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.function.Operation;
import org.pot.common.serialization.SerializeUtil;
import org.pot.common.util.LogUtil;
import org.pot.core.util.SignalLight;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.world.WorldManager;
import org.pot.game.engine.world.module.map.born.PlayerBornRule;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.PlayerProfileEntity;
import org.pot.game.persistence.mapper.PlayerProfileEntityMapper;
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
        String caller = ExceptionUtil.getCaller(this.getClass(), Player.class, PlayerGroup.class, PlayerManager.class);
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                update(caller, onSuccess, onFail);
            }
        });
    }

    private void update(String caller, Operation onSuccess, Operation onFail) {
        try {
            SqlSession sqlSession = GameDb.local().getSqlSession(uid);
            sqlSession.getMapper(PlayerProfileEntityMapper.class).insertOnDuplicateKeyUpdate(profile);
            if (onSuccess != null) onSuccess.operate();
        } catch (Throwable ex) {
            log.error("Update Player Error!uid={},caller={},dump={}", uid, caller, dump(), ex);
            if (onFail != null) onFail.operate();
        }
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
        try {
            SqlSession sqlSession = GameDb.local().getSqlSession(uid);
            sqlSession.getMapper(PlayerProfileEntityMapper.class).insert(profile);
            if (onSuccess != null) onSuccess.operate();
        } catch (Throwable ex) {
            log.error("Insert Player Error!uid={},caller={},dump={}", uid, caller, dump(), ex);
            if (onFail != null) onFail.operate();
        }
    }

    public void asyncDelete(Operation onSuccess, Operation onFail) {
        String caller = ExceptionUtil.getCaller(this.getClass(), Player.class, PlayerGroup.class, PlayerManager.class);
        asyncLoad(playerData -> delete(caller, onSuccess, onFail), playerData -> log.error("Delete Player Load Fail! uid={}", uid));
    }

    public void delete(String caller, Operation onSuccess, Operation onFail) {
        if (profile.getServerId() == GameServerInfo.getServerId()) {
            if (onFail != null) onFail.operate();
            return;
        }
        try {
            SqlSession sqlSession = GameDb.local().getSqlSession(uid);
            sqlSession.getMapper(PlayerProfileEntityMapper.class).delete(profile);
            if (onSuccess != null) onSuccess.operate();
        } catch (Throwable ex) {
            log.error("Delete Player Error!uid={},caller={},dump={}", uid, caller, dump(), ex);
            if (onFail != null) onFail.operate();
        }
    }

    public void load(String caller, Consumer<PlayerData> onSuccess, Consumer<PlayerData> onFail) {
        try {
            SqlSession sqlSession = GameDb.local().getSqlSession(uid);
            profile = sqlSession.getMapper(PlayerProfileEntityMapper.class).select(uid);
            if (profile == null) {
                loadSuccess = false;
                loadOver = true;
                if (onFail != null) {
                    onFail.accept(this);
                }
            }
            loadSuccess = true;
            loadOver = true;
            if (onSuccess != null) onSuccess.accept(this);
        } catch (Throwable ex) {
            log.error("Load Player Error!uid={},caller={},dump={}", uid, caller, dump(), ex);
            loadOver = true;
            loadSuccess = false;
            if (onFail != null) onFail.accept(this);
        }
    }

    public PlayerProfileEntity onRegister(LoginDataS2S loginDataS2S) {
        if (loginDataS2S == null) {
            throw new ServiceException(LogUtil.format("LoginData Register Error uid={}", uid));
        }
        PlayerProfileEntity temp = new PlayerProfileEntity();
        temp.setUid(loginDataS2S.getGameUid());
        temp.setName(PlayerCaches.name().newUniqueName(loginDataS2S.getGameUid()));
        temp.setAccount(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getAccount()));
        temp.setAccountUid(loginDataS2S.getAccountUid());
        temp.setDevice(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getDevice()));
        temp.setServerId(loginDataS2S.getServerId());
        temp.setUnionId(0);
        temp.setRegTime(System.currentTimeMillis());
        temp.setRegDeviceOs(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getDeviceOS()));
        temp.setLoginIp(loginDataS2S.getIp());
        temp.setLoginDeviceOs(StringUtils.stripToEmpty(loginDataS2S.getLoginReqC2S().getDeviceOS()));
        temp.setLastLoginTime(System.currentTimeMillis());
        temp.setLastOnlineTime(System.currentTimeMillis());
        temp.setOnlineTime(System.currentTimeMillis());
        int worldPointId = WorldManager.getInstance().execute(()-> PlayerBornRule.getInstance().bornPlayer(this.uid));
        temp.setWorldPointId(worldPointId);
        temp.setWorldCleanTime(0L);
        temp.setPayTotalDiamond(0);
        temp.setPayTotalPrice(0);
        temp.setPayFirstTime(0);
        temp.setAppVersion(loginDataS2S.getLoginReqC2S().getAppVersion());
        temp.setOsVersion(loginDataS2S.getLoginReqC2S().getOsVersion());
        temp.setChannel(loginDataS2S.getLoginReqC2S().getChannel());
        temp.setPlatform(loginDataS2S.getLoginReqC2S().getPlatform());
        temp.setCountry(loginDataS2S.getCountry());
        temp.setLanguage(loginDataS2S.getDeviceLanguage());
        temp.setCid(loginDataS2S.getLoginReqC2S().getCid());
        temp.setChatBan(0L);
        temp.setRenameBan(0L);
        temp.setTranslateLanguage(loginDataS2S.getDeviceLanguage());
        profile = temp;
        return temp;
    }

    public String dump() {
        try {
            byte[] bytes = SerializeUtil.serialize(this);
            Compressor compressor = new GzipCompressor();
            return Base64Util.encode(compressor.compress(bytes));
        } catch (Throwable ex) {
            return StringUtils.EMPTY;
        }
    }
}
