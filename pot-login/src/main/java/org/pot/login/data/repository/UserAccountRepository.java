package org.pot.login.data.repository;

import org.pot.login.domain.object.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Description: User: zouhaiyang Date: 2021-04-12
 */
@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

}
