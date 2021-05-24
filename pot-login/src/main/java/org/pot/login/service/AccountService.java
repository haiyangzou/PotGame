package org.pot.login.service;

import lombok.extern.slf4j.Slf4j;
import org.pot.core.data.cache.AbstractCache;
import org.pot.login.data.repository.AccountDataRepository;
import org.pot.login.domain.object.AccountsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Description: User: zouhaiyang Date: 2021-04-14
 */
@Component
public class AccountService extends AbstractCache<Long, AccountsData> {

	@Autowired
	private AccountDataRepository accountRepository;

	public AccountService(
			CrudRepository<AccountsData, Long> repository) {
		super(repository);
	}

	@Override
	public AccountsData defaultInstance(Long key) {
		AccountsData account = get(key);
		if (account == null) {
			account = new AccountsData();
			account.setOpenid(key);
			save(key, account);
		}
		return account;
	}

	@Override
	public AccountsData load(Long openId) throws Exception {
		AccountsData account = accountRepository.findById(openId).orElse(null);
		return account;
	}

	@Override
	public void write(Long key, AccountsData value) throws Exception {
		accountRepository.save(value);
	}

}
