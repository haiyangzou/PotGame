package org.pot.game.engine.world.module.map.clean;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.MapUtil;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.module.map.scene.WorldMapPointRegulation;
import org.pot.game.engine.world.module.map.scene.WorldMapScene;
import org.pot.game.engine.world.module.var.WorldVarDef;
import org.pot.game.resource.clean.CleanPlayerRule;
import org.pot.game.resource.clean.CleanPlayerRuleConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WorldMapCleaner {
    private volatile int cleanMin;
    @Getter
    private volatile int cleanMax;
    @Getter
    private volatile int cleanInterval;
    private volatile long lastCLeanTimestamp;
    private volatile boolean cleaning = false;
    private volatile Map<Integer, List<WorldMapBlockCleanInfo>> cleanMap = Collections.emptyMap();
    private volatile RunSignal cleanSignal = new RunSignal(TimeUnit.SECONDS.toMillis(1));
    private volatile RunSignal checkCLeanSignal = new RunSignal(TimeUnit.MINUTES.toMillis(1));

    public void init() {
        lastCLeanTimestamp = System.currentTimeMillis();
        cleanMin = WorldVarDef.IntVar.WorldMapCleanMin.value(9600);
        cleanMax = WorldVarDef.IntVar.WorldMapCleanMax.value(12000);
        cleanInterval = WorldVarDef.IntVar.WorldMapCleanInterval.value(30);
    }

    public void save() {
        if (cleanMin == 9600) {
            WorldVarDef.IntVar.WorldMapCleanMin.delete();
        } else {
            WorldVarDef.IntVar.WorldMapCleanMin.update(cleanMin);
        }
        if (cleanMax == 12000) {
            WorldVarDef.IntVar.WorldMapCleanMax.delete();
        } else {
            WorldVarDef.IntVar.WorldMapCleanMax.update(cleanMax);
        }
        if (cleanInterval == 30) {
            WorldVarDef.IntVar.WorldMapCleanInterval.delete();
        } else {
            WorldVarDef.IntVar.WorldMapCleanInterval.update(cleanInterval);
        }
    }

    public void tick() {
        if (cleaning) {
            cleanSignal.run(this::clean);
            return;
        }
        if (cleanInterval <= 0) {
            return;
        }
        if (checkCLeanSignal.signal()) {
            long capacity = WorldMapScene.singleton.getPointManager().countPointType(PointType.CITY);
            if (capacity > cleanMax) {
                log.info("Cleaner Capacity Over Max,capacity={},cleaning={},cleanMin={},cleanMax={},cleanInterval={}",
                        capacity, cleaning, cleanMin, cleanMax, cleanInterval);
                mark();
            } else if (capacity >= cleanMin && capacity <= cleanMax) {
                long nextCleanTimestamp = lastCLeanTimestamp + TimeUnit.MINUTES.toMillis(cleanInterval);
                if (nextCleanTimestamp < System.currentTimeMillis()) {
                    log.info("Cleaner Period,capacity={},cleaning={},cleanMin={},cleanMax={},cleanInterval={}",
                            capacity, cleaning, cleanMin, cleanMax, cleanInterval);
                    mark();
                }
            }
        }
    }

    private void mark() {
        cleaning = true;
        cleanMap = new HashMap<>();
        lastCLeanTimestamp = System.currentTimeMillis();
        List<CleanPlayerRule> rules = CleanPlayerRuleConfig.getInstance().getCleanPlayerRules();
        long ruleCleanIntervalMillis = TimeUnit.MINUTES.toMillis(cleanInterval / rules.size());
        for (int i = 0; i < rules.size(); i++) {
            CleanPlayerRule rule = rules.get(i);
            long ruleOffsetMillis = i * ruleCleanIntervalMillis;
            List<WorldMapBlockCleanInfo> cleans = new ArrayList<>();
            List<Integer> disorderlyBlockIds = WorldMapPointRegulation.getDisorderlyBlockIds();
            for (int j = 0; j < disorderlyBlockIds.size(); j++) {
                Integer disorderlyBlockId = disorderlyBlockIds.get(j);
                long blockOffsetMillis = j * (ruleCleanIntervalMillis / disorderlyBlockIds.size());
                cleans.add(new WorldMapBlockCleanInfo(disorderlyBlockId, lastCLeanTimestamp + ruleOffsetMillis + blockOffsetMillis));
            }
            cleanMap.put(rule.getId(), cleans);
        }
        log.info("Cleaner Start World Map,cleaning={},cleanMin={},cleanMax={},cleanInterval={}",
                cleaning, cleanMin, cleanMax, cleanInterval);
    }

    private void clean() {
        if (MapUtil.isEmpty(cleanMap)) {
            cleaning = false;
            log.info("Cleaner Finish World Map,cleaning={},cleanMin={},cleanMax={},cleanInterval={}",
                    cleaning, cleanMin, cleanMax, cleanInterval);
            return;
        }
        List<CleanPlayerRule> rules = CleanPlayerRuleConfig.getInstance().getCleanPlayerRules();
        for (CleanPlayerRule rule : rules) {
            List<WorldMapBlockCleanInfo> blockCleans = cleanMap.get(rule.getId());
            if (blockCleans == null) continue;
            boolean satisfy = false;
            Iterator<WorldMapBlockCleanInfo> iterator = blockCleans.iterator();
            while (iterator.hasNext()) {
                WorldMapBlockCleanInfo blockCleanInfo = iterator.next();
                if (blockCleanInfo.getCleanTimestamp() > System.currentTimeMillis()) {
                    break;
                }
                cleanBlock(rule, blockCleanInfo);
                iterator.remove();
                long capacity = WorldMapScene.singleton.getPointManager().countPointType(PointType.CITY);
                log.debug("Cleaner Finish Block");
                if (capacity > cleanMin) {
                    satisfy = true;
                    break;
                }
            }
            if (CollectionUtil.isEmpty(blockCleans)) {
                cleanMap.remove(rule.getId());
                long capacity = WorldMapScene.singleton.getPointManager().countPointType(PointType.CITY);
                log.debug("Cleaner Finish Block");
                if (capacity > cleanMin) {
                    satisfy = true;
                }
            }
            if (satisfy) {
                long capacity = WorldMapScene.singleton.getPointManager().countPointType(PointType.CITY);
                log.info("Cleaner Finish Cause Capacity Enough,capacity={},ruleLv={},cleaning={},cleanMin={},cleanMax={},cleanInterval={}",
                        capacity, rule.getLv(), cleaning, cleanMin, cleanMax, cleanInterval);
                cleanMap.clear();
                cleaning = false;
                break;
            }
        }

    }

    private void cleanBlock(CleanPlayerRule rule, WorldMapBlockCleanInfo blockCleanInfo) {
        List<Integer> blockPointIds = WorldMapPointRegulation.getBlock(blockCleanInfo.getBlockId()).getPointIds();
        List<WorldPoint> blockCities = WorldMapScene.singleton.getPointManager().getMainPoints(blockPointIds, PointType.CITY);
        for (WorldPoint blockCity : blockCities) {
            final PointCityData pointCityData = blockCity.getExtraData(PointCityData.class);
            if (pointCityData == null) continue;
            final Player cityPlayer = PlayerManager.fetchPlayer(pointCityData.getPlayerId());
            if (cityPlayer == null) continue;
            //TODO 清理限制
        }
    }


}
