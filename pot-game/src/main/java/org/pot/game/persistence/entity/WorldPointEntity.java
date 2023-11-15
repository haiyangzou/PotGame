package org.pot.game.persistence.entity;

import lombok.*;
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
@Table(name = "world_point")
@Getter
public class WorldPointEntity implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "type")
    private Integer type;
    @Column(name = "x")
    private Integer x;
    @Column(name = "y")
    private Integer y;
    @Column(name = "mainX")
    private Integer mainX;
    @Column(name = "mainY")
    private Integer mainY;
    @Column(name = "extra_data")
    private PointExtraData extraData;
}
