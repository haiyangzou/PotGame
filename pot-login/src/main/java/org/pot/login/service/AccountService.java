package org.pot.login.service;

import org.pot.core.data.cache.AbstractCache;
import org.pot.login.data.repository.UserAccountRepository;
import org.pot.login.domain.object.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Description: User: zouhaiyang Date: 2021-04-14
 */
@Component
public class AccountService extends AbstractCache<Long, UserAccount> {

    @Autowired
    private UserAccountRepository accountRepository;

    public AccountService(CrudRepository<UserAccount, Long> repository) {
        super(repository);
    }

    @Override
    public UserAccount defaultInstance(Long key) {
        UserAccount account = get(key);
        if (account == null) {
            account = new UserAccount();
            account.setUid(key);
            save(key, account);
        }
        return account;
    }

    @Override
    public UserAccount load(Long openId) throws Exception {
        UserAccount account = accountRepository.findById(openId).orElse(null);
        return account;
    }

    @Override
    public void write(Long key, UserAccount value) throws Exception {
        accountRepository.save(value);
    }

}
