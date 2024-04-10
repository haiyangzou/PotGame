package org.pot.game.util;

import org.pot.common.util.RandomUtil;
import org.pot.dal.DbSupport;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.drop.DropRecord;
import org.pot.game.engine.player.Player;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.DropRecordEntity;
import org.pot.game.persistence.mapper.DropRecordEntityMapper;
import org.pot.game.resource.common.Drop;
import org.pot.game.resource.common.Reward;
import org.pot.game.resource.drop.DropInfo;
import org.pot.game.resource.drop.DropInfoConfig;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerDropUtil {
    private static DropInfo doRandomDrop(List<DropInfo> dropInfos) {
        if (dropInfos == null || dropInfos.isEmpty())
            return null;
        int totalRate = ((DropInfo) dropInfos.get(0)).getTotalRate();
        int random = RandomUtil.randomInt(totalRate) + 1;
        int currValue = 0;
        for (DropInfo drop : dropInfos) {
            currValue += drop.getRate();
            if (random <= currValue)
                return drop;
        }
        return null;
    }

    private static void randomDropInfo(@Nonnull Player player, @Nonnull Drop drop, @Nonnull Consumer<DropInfo> consumer) {
        int dropNo = drop.getId();
        List<DropInfo> dropInfoList = DropInfoConfig.getInstance().getDropInfoList(dropNo);
        List<DropInfo> defaultDrop = getDefaultDrop(dropInfoList);
        for (int i = drop.getCount(); i > 0; i--) {
            DropInfo dropInfo = doRandomDrop(dropInfoList);
            if (dropInfo != null) {
                boolean useDefault = (dropInfo.getMinLevel() > 0 || dropInfo.getMaxLevel() > 0) && (dropInfo.getMinLevel() > 0 && player.commonAgent.getLevel() < dropInfo.getMinLevel() || dropInfo
                        .getMaxLevel() > 0 && player.commonAgent.getLevel() > dropInfo.getMaxLevel());
                if (!useDefault && dropInfo.getMaxPersonalCount() > 0) {
                    DropRecord playerDropRecord = getPlayerDropRecord(player, dropNo, dropInfo.getTimePeriod());
                    if (!addDropCount(playerDropRecord, 1, dropInfo.getMaxPersonalCount()))
                        useDefault = true;
                }
                if (useDefault) {
                    for (DropInfo info : defaultDrop)
                        consumer.accept(info);
                } else {
                    consumer.accept(dropInfo);
                }
            }
        }
    }

    private static List<DropInfo> getDefaultDrop(List<DropInfo> dropInfoList) {
        return (dropInfoList != null) ? dropInfoList.stream().filter(dropInfo -> (dropInfo.getIsDefault() == 1)).collect(Collectors.toList()) : new ArrayList<>();
    }

    public static void randomDrop(@Nonnull Player player, @Nonnull Drop drop, @Nonnull Consumer<Reward> consumer) {
        randomDropInfo(player, drop, dropInfo -> consumer.accept(dropInfo.getReward()));
    }

    public static void randomDrop(Player player, Drop[] drops, @Nonnull Consumer<Reward> consumer) {
        if (drops != null)
            for (Drop drop : drops) {
                if (drop != null)
                    randomDrop(player, drop, consumer);
            }
    }

    public static void randomDropInfo(Player player, Drop drop, List<DropInfo> dropInfos) {
        randomDropInfo(player, drop, dropInfos::add);
    }

    public static List<Reward> randomDrop(Player player, Drop... drops) {
        if (drops == null || drops.length == 0)
            return Collections.emptyList();
        List<Reward> rewards = new ArrayList<>();
        for (Drop drop : drops) {
            if (drop != null)
                randomDrop(player, drop, rewards::add);
        }
        return rewards;
    }

    public static List<Reward> randomDrop(Player player, int dropNo, int count) {
        Drop drop = new Drop(dropNo, count);
        return randomDrop(player, new Drop[]{drop});
    }

    private static DropRecord getPlayerDropRecord(Player player, int dropId, int timePeriod) {
        Map<String, DropRecord> dropRecordMap = player.dropAgent.getDropRecord();
        DropRecord dropRecord = dropRecordMap.computeIfAbsent(String.valueOf(dropId), k -> new DropRecord(String.valueOf(dropId), LocalDate.now(ZoneOffset.UTC), timePeriod));
        if (checkTimeOut(dropRecord)) {
            dropRecordMap.remove(String.valueOf(dropId));
            dropRecord = new DropRecord(String.valueOf(dropId), LocalDate.now(ZoneOffset.UTC), timePeriod);
        }
        return dropRecord;
    }

    private static synchronized boolean globalDropRecordAddDropCount(int dropId, int addCount, int maxCount, int timePeriod) {
        SqlSession sqlSession = GameDb.local().getSqlSession(dropId);
        DropRecordEntity dropRecordEntity = sqlSession.getMapper(DropRecordEntityMapper.class).select(-1);
        boolean needSave = false;
        if (dropRecordEntity == null) {
            dropRecordEntity = new DropRecordEntity();
            dropRecordEntity.setId(-1L);
            dropRecordEntity.setDetail(new HashMap<>());
            needSave = true;
        }
        Map<String, DropRecord> dropRecordMap = dropRecordEntity.getDetail();
        DropRecord dropRecord = dropRecordMap.computeIfAbsent(String.valueOf(dropId), k -> new DropRecord(String.valueOf(dropId), LocalDate.now(ZoneOffset.UTC), timePeriod));
        if (checkTimeOut(dropRecord)) {
            dropRecordMap.remove(String.valueOf(dropId));
            dropRecord = new DropRecord(String.valueOf(dropId), LocalDate.now(ZoneOffset.UTC), timePeriod);
        }
        boolean dropCount = addDropCount(dropRecord, addCount, maxCount);
        if (dropCount)
            if (needSave) {
                sqlSession.getMapper(DropRecordEntityMapper.class).insert(dropRecordEntity);
            } else {
                sqlSession.getMapper(DropRecordEntityMapper.class).update(dropRecordEntity);
            }
        return dropCount;
    }

    private static boolean checkTimeOut(DropRecord dropRecord) {
        long between = ChronoUnit.DAYS.between(dropRecord.getDate(), LocalDate.now(ZoneOffset.UTC));
        return (between >= dropRecord.getTimePeriod());
    }

    private static boolean addDropCount(DropRecord dropRecord, int addCount, int maxCount) {
        int totalCount = dropRecord.getCount() + addCount;
        if (totalCount > maxCount)
            return false;
        dropRecord.setCount(totalCount);
        return true;
    }

}
