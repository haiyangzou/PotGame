package org.pot.game.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.player.module.hero.CardsPoolInfo;
import org.pot.game.engine.player.module.hero.PlayerHero;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "player_hero")
public class PlayerHeroEntity implements Serializable {
    @Id
    @Column(name = "id", columnDefinition = "bigint NOT NULL COMMENT")
    private Long id;
    @Column(name = "hero_info", columnDefinition = "longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT")
    private List<PlayerHero> heroInfo;
    @Column(name = "pub_info", columnDefinition = "longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT")
    private Map<Integer, CardsPoolInfo> pubInfoMap = new HashMap<>();

}
