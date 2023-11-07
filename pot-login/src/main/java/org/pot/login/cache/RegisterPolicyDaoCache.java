package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.pot.login.dao.RegisterPolicyDao;
import org.pot.login.entity.RegisterPolicy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class RegisterPolicyDaoCache {
    private final AtomicReference<List<RegisterPolicy>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterPolicyDao registerPolicyDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterPolicy> nextList = ImmutableList.copyOf(registerPolicyDao.selectAll());
    }

    @PostConstruct
    private void init() {
        executeJob();
    }

    public List<RegisterPolicy> selectAll() {
        List<RegisterPolicy> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerPolicyDao.selectAll();
    }
}
