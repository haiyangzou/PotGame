package org.pot.login.service;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.id.UniqueIdUtil;
import org.pot.login.dao.TickedDao;
import org.pot.login.entity.Ticked;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UniqueIdService {
    @Resource
    private TickedDao tickedDao;

    public synchronized long getTickedId() {
        Ticked ticked = new Ticked();
        tickedDao.generateKey(ticked);
        return ticked.getId();
    }

    public long newUniqueId(int serverId) {
        return UniqueIdUtil.newUniqueId(getTickedId(), serverId);
    }
}
