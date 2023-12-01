package org.pot.game.gate;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.player.PlayerState;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.PlayerGhostEntity;
import org.pot.game.persistence.mapper.PlayerGhostEntityMapper;
import org.pot.game.persistence.mapper.PlayerProfileEntityMapper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GhostUtil {
    public static void save(PlayerGhostEntity entity) {
        if (entity == null) return;
        SqlSession sqlSession = GameDb.local().getSqlSession(entity.getPlayerId());
        sqlSession.submitWithoutResult(
                PlayerGhostEntityMapper.class,
                m -> m.insertOnDuplicateKeyUpdate(entity),
                () -> log.info("save ghost  player success"),
                () -> log.error("save ghost player failed")
        );
    }

    public static void delete(long playerUid) {
        SqlSession sqlSession = GameDb.local().getSqlSession(playerUid);
        sqlSession.submitWithoutResult(
                PlayerGhostEntityMapper.class,
                m -> m.delete(playerUid),
                () -> log.info("delete ghost  player success"),
                () -> log.error("delete ghost player failed")
        );
    }

    public static void load() {
        SqlSession sqlSession = GameDb.local().getSqlSession(PlayerGhostEntityMapper.class);
        List<Long> allPlayerIds = sqlSession.getMapper(PlayerProfileEntityMapper.class).selectAllPlayerIds();
        List<PlayerGhostEntity> playerGhostEntities = sqlSession.getMapper(PlayerGhostEntityMapper.class).all();
        final Set<Long> loadedGhostSet = Sets.newConcurrentHashSet();
        Iterator<PlayerGhostEntity> iterator = playerGhostEntities.iterator();
        while (iterator.hasNext()) {
            PlayerGhostEntity entity = iterator.next();
            if (allPlayerIds.contains(entity.getPlayerId())) {
                PlayerAsyncTask.submit(entity.getPlayerId(), player -> {
                    player.ghostAgent.loadEntity(entity);
                    loadedGhostSet.add(entity.getPlayerId());
                });
            } else {
                iterator.remove();
                delete(entity.getPlayerId());
            }
        }
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> {
            boolean success = true;
            for (PlayerGhostEntity playerGhostEntity : playerGhostEntities) {
                Player player = PlayerManager.fetchPlayer(playerGhostEntity.getPlayerId());
                success &= player != null && player.getState().get() == PlayerState.running && loadedGhostSet.contains(player.getUid());
            }
            return success;
        });
    }
}
