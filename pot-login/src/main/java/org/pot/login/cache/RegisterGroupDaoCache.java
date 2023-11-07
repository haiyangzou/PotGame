package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.pot.login.dao.RegisterGroupDao;
import org.pot.login.entity.RegisterGroup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class RegisterGroupDaoCache {
    private final AtomicReference<List<RegisterGroup>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterGroupDao registerGroupDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterGroup> nextList = ImmutableList.copyOf(registerGroupDao.selectAll());
    }

    public List<RegisterGroup> selectAll() {
        List<RegisterGroup> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerGroupDao.selectAll();
    }
}
