package org.pot.game.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.point.PointExtraData;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "world_point")
@Getter
@Setter
public class WorldPointEntity implements Serializable {
    @Id
    private Integer id;
    private Integer type;
    private Integer x;
    private Integer y;
    private Integer mainX;
    private Integer mainY;
    private PointExtraData extraData;
}
