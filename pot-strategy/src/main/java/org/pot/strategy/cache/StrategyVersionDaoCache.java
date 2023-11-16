package org.pot.strategy.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.communication.strategy.StrategyUtil;
import org.pot.common.communication.strategy.StrategyVersion;
import org.pot.common.util.MapUtil;
import org.pot.strategy.dao.StrategyVersionDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StrategyVersionDaoCache {
    private final AtomicReference<List<StrategyVersion>> listReference = new AtomicReference<>(null);
    private final AtomicReference<Map<String, Map<String, StrategyVersion>>> mapReference = new AtomicReference<>(null);
    @Resource
    private StrategyVersionDao strategyVersionDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<StrategyVersion> nextList = ImmutableList.copyOf(strategyVersionDao.selectAll());
        List<StrategyVersion> prevList = listReference.getAndUpdate(value -> nextList);
        Map<String, Map<String, StrategyVersion>> map = nextList.stream().collect(Collectors.groupingBy(StrategyVersion::getVersion, Collectors.toMap((StrategyVersion v) -> StringUtils.stripToEmpty(v.getPackageName()), v -> v)));
        mapReference.getAndUpdate(value -> MapUtil.immutableMapMap(map));
        StrategyUtil.setVersionList(listReference.get());
        StrategyUtil.setVersionMap(mapReference.get());
    }

    @PostConstruct
    private void init() {
        executeJob();
    }
}
