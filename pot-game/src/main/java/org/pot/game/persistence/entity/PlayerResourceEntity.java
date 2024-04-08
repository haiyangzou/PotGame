package org.pot.game.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "player_resource")
public class PlayerResourceEntity {
    @Id
    @Column(name = "id", columnDefinition = "bigint NOT NULL COMMENT ")
    private Long id;
    @Column(name = "resource_info", columnDefinition = "longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT")
    private Map<Integer, Long> resourceInfo;
}
