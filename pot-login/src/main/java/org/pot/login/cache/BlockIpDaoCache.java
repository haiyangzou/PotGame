package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import org.pot.common.util.MapUtil;
import org.pot.login.dao.BlockIpDao;
import org.pot.login.entity.BlockIp;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BlockIpDaoCache {
    @Resource
    private BlockIpDao blockIpDao;
    private final AtomicReference<List<BlockIp>> listReference = new AtomicReference<>();

    private final AtomicReference<Map<String, BlockIp>> mapReference = new AtomicReference<>(null);

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<BlockIp> nextList = ImmutableList.copyOf(blockIpDao.selectAll());
        List<BlockIp> prevList = listReference.getAndUpdate(value -> nextList);
        mapReference.getAndUpdate(value -> MapUtil.immutableMap(nextList, BlockIp::getBlockIp));
    }

    public BlockIp selectOne(String ip) {
        Map<String, BlockIp> map = mapReference.get();
        if (map != null) return map.get(ip);
        return blockIpDao.selectOne(ip);
    }
}
