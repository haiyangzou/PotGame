package org.pot.login.cache;

import com.google.common.collect.ImmutableList;
import org.pot.login.dao.RegisterLocaleDao;
import org.pot.login.entity.RegisterLocale;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RegisterLocaleDaoCache {
    private final AtomicReference<List<RegisterLocale>> listReference = new AtomicReference<>(null);
    @Resource
    private RegisterLocaleDao registerLocaleDao;

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeJob() {
        List<RegisterLocale> nextList = ImmutableList.copyOf(registerLocaleDao.selectAll());
        List<RegisterLocale> prevList = listReference.getAndUpdate(value -> nextList);
    }

    public List<RegisterLocale> selectAll() {
        List<RegisterLocale> list = listReference.get();
        if (list != null) return list;
        executeJob();
        return registerLocaleDao.selectAll();
    }
}
