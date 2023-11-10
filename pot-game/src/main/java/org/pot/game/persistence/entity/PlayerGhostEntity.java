package org.pot.game.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "playerId")
@Table(name = "player_ghost")
public class PlayerGhostEntity implements Serializable {
    @Id
    @Getter
    @Column(name = "player_id", columnDefinition = "bigint NOT NULL")
    private Long playerId;
    @Getter
    @Column(name = "destroyed")
    private Boolean destroyed;
    @Getter
    @Column(name = "visa_data")
    private String visaData;
}
