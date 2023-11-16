package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import org.pot.login.dao.RegisterPolicyGroupDao;
import org.pot.login.entity.RegisterPolicyGroup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RegisterPolicyGroupDaoCache {
    private final AtomicReference<List<RegisterPolicyGroup>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterPolicyGroupDao registerPolicyGroupDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterPolicyGroup> nextList = ImmutableList.copyOf(registerPolicyGroupDao.selectAll());
        List<RegisterPolicyGroup> prevList = listReference.getAndUpdate(value -> nextList);
    }

    public List<RegisterPolicyGroup> selectAll() {
        List<RegisterPolicyGroup> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerPolicyGroupDao.selectAll();
    }
}
