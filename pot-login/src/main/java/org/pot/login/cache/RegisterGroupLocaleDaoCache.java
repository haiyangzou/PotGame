package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.pot.login.dao.RegisterGroupLocaleDao;
import org.pot.login.entity.RegisterGroupLocale;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class RegisterGroupLocaleDaoCache {
    private final AtomicReference<List<RegisterGroupLocale>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterGroupLocaleDao registerGroupLocaleDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterGroupLocale> nextList = ImmutableList.copyOf(registerGroupLocaleDao.selectAll());
        List<RegisterGroupLocale> prevList = listReference.getAndUpdate(value -> nextList);
    }

    public List<RegisterGroupLocale> selectAll() {
        List<RegisterGroupLocale> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerGroupLocaleDao.selectAll();
    }
}
