package org.pot.game.engine.world.module.map.scene;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.MarchRegulation;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.WorldMarchEntity;
import org.pot.game.persistence.mapper.WordMarchEntityMapper;
import org.pot.game.persistence.mapper.WorldPointEntityMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class WorldMapMarchRegulation extends MarchRegulation {
    public WorldMapMarchRegulation(AbstractScene scene) {
        super(scene);
    }

    @Override
    public AbstractScene getScene() {
        return (WorldMapScene) scene;
    }

    @Override
    public void save(boolean async) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WordMarchEntityMapper.class);
        Collection<March> marches = scene.getMarchManager().getMarches();
        if (marches.isEmpty()) {
            sqlSession.submitWithoutResult(WordMarchEntityMapper.class,
                    m -> m.truncate(false),
                    () -> log.info("truncate world march success"),
                    () -> log.error("truncate world march failed"));
            return;
        }
        Runnable runnable = () -> {
            log.info("save world march start");
            List<WorldMarchEntity> worldMarchEntities = Lists.newArrayListWithExpectedSize(marches.size());
            for (March march : marches) {
                worldMarchEntities.add(toWorlMarchEntity(march));
            }
            sqlSession.submitWithoutResult(WordMarchEntityMapper.class,
                    m -> m.batchInsertOnDuplicateKeyUpdate(worldMarchEntities),
                    () -> log.info("save world march success"),
                    () -> log.error("save world march failed"));
            log.info("delete invalid world march start");
            sqlSession.submitWithoutResult(WordMarchEntityMapper.class,
                    m -> m.deleteNoInidList(worldMarchEntities),
                    () -> log.info("delete world march success"),
                    () -> log.error("delete world march failed"));
        };
        if (async) {
            GameEngine.getInstance().getAsyncExecutor().execute(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public void init() {
        SqlSession sqlSession = GameDb.local().getSqlSession(WordMarchEntityMapper.class);
        WordMarchEntityMapper worldMarchEntityMapper = sqlSession.getMapper(WordMarchEntityMapper.class);
        List<March> marches = worldMarchEntityMapper.all().stream()
                .map(WorldMarchEntity::getMarchData)
                .sorted((m1, m2) -> -Long.compare(Long.parseLong(m1.getId()), Long.parseLong(m2.getId())))
                .collect(Collectors.toList());
        MarchManager marchManager = scene.getMarchManager();
        marches.forEach(marchManager::addMarch);
    }

    @Override
    public void tick() {

    }

    public void onMarchAdd(March march) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WordMarchEntityMapper.class);
        sqlSession.submitWithoutResult(WordMarchEntityMapper.class, m -> m.insertOnDuplicateKeyUpdate(toWorlMarchEntity(march)));
    }

    public void onMarchRemove(March march) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WordMarchEntityMapper.class);
        sqlSession.submitWithoutResult(WordMarchEntityMapper.class, m -> m.deleteById(Long.parseLong(march.getId())));

    }

    public void onMarchUpdate(March march) {
        SqlSession sqlSession = GameDb.local().getSqlSession(WordMarchEntityMapper.class);
        sqlSession.submitWithoutResult(WordMarchEntityMapper.class, m -> m.insertOnDuplicateKeyUpdate(toWorlMarchEntity(march)));

    }

    private WorldMarchEntity toWorlMarchEntity(March march) {
        return WorldMarchEntity.builder()
                .id(Long.parseLong(march.getId()))
                .type(march.getType().getId())
                .state(march.getState().getId())
                .ownerId(march.getOwnerId())
                .marchData(march)
                .build();
    }
}
