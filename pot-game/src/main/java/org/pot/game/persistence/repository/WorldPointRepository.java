package org.pot.game.persistence.repository;

import org.pot.game.persistence.entity.WorldPointEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Description: User: zouhaiyang Date: 2021-04-12
 */
@Repository
public interface WorldPointRepository extends CrudRepository<WorldPointEntity, Long> {

}
