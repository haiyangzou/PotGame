package org.pot.strategy.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.strategy.StrategyDeviceName;
import org.pot.common.communication.strategy.StrategyUtil;
import org.pot.common.util.MapUtil;
import org.pot.strategy.dao.StrategyDeviceNameDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class StrategyDeviceNameDaoCache {
    private final AtomicReference<List<StrategyDeviceName>> listReference = new AtomicReference<>();
    private final AtomicReference<Map<String, StrategyDeviceName>> mapReference = new AtomicReference<>(null);
    @Resource
    private StrategyDeviceNameDao strategyDeviceNameDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<StrategyDeviceName> nextList = ImmutableList.copyOf(strategyDeviceNameDao.selectValidAll());
        mapReference.getAndUpdate(value -> ImmutableMap.copyOf(MapUtil.toHashMap(nextList, StrategyDeviceName::getDeviceName)));
        StrategyUtil.setDeviceNameList(listReference.get());
        StrategyUtil.setDeviceNameMap(mapReference.get());
    }

    @PostConstruct
    private void init() {
        executeJob();
    }
}
