package org.pot.login.data.repository;

import org.pot.login.domain.object.AccountsData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Description: User: zouhaiyang Date: 2021-04-12
 */
@Repository
public interface AccountDataRepository extends CrudRepository<AccountsData, Long> {

}
