package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.pot.login.dao.RegisterServerPolicyDao;
import org.pot.login.entity.RegisterServerPolicy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class RegisterServerPolicyDaoCache {
    private final AtomicReference<List<RegisterServerPolicy>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterServerPolicyDao registerServerPolicyDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterServerPolicy> nextList = ImmutableList.copyOf(registerServerPolicyDao.selectAll());
        List<RegisterServerPolicy> prevList = listReference.getAndUpdate(value -> nextList);
    }

    public List<RegisterServerPolicy> selectAll() {
        List<RegisterServerPolicy> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerServerPolicyDao.selectAll();
    }
}
