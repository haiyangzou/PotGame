package org.pot.strategy.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.strategy.StrategyIp;
import org.pot.common.communication.strategy.StrategyUtil;
import org.pot.common.util.MapUtil;
import org.pot.strategy.dao.StrategyIpDao;
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
public class StrategyIpDaoCache {
    private final AtomicReference<List<StrategyIp>> listReference = new AtomicReference<>();
    private final AtomicReference<Map<String, StrategyIp>> mapReference = new AtomicReference<>(null);
    @Resource
    private StrategyIpDao strategyIpDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<StrategyIp> nextList = ImmutableList.copyOf(strategyIpDao.selectValidAll());
        mapReference.getAndUpdate(value -> ImmutableMap.copyOf(MapUtil.toHashMap(nextList, StrategyIp::getIp)));
        StrategyUtil.setIpList(listReference.get());
        StrategyUtil.setIpMap(mapReference.get());
    }

    @PostConstruct
    private void init() {
        executeJob();
    }
}
