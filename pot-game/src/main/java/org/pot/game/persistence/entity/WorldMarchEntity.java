package org.pot.game.persistence.entity;

import lombok.*;
import org.pot.game.engine.march.March;
import org.pot.game.engine.point.PointExtraData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@Entity
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "world_march")
@Getter
@Setter
public class WorldMarchEntity implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "type")
    private Integer type;
    @Column(name = "state")
    private Integer state;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "march_data")
    private March marchData;
}
