package org.pot.login.cache;

import org.pot.login.dao.BlockIpDao;
import org.pot.login.entity.BlockIp;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BlockIpDaoCache {
    @Resource
    private BlockIpDao blockIpDao;
    private final AtomicReference<Map<String, BlockIp>> mapReference = new AtomicReference<>(null);

    public BlockIp selectOne(String ip) {
        Map<String, BlockIp> map = mapReference.get();
        if (map != null) return map.get(ip);
        return blockIpDao.selectOne(ip);
    }
}
