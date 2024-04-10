package org.pot.game.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.drop.DropRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;
@Getter
@Setter
@Entity
@Table(name = "drop_record")
public class DropRecordEntity implements Serializable {
    @Id
    @Column(name = "id", columnDefinition = "bigint NOT NULL COMMENT '(-1表示全服)'")
    private Long id;

    @Column(name = "detail", columnDefinition = "longtext COMMENT '掉落数据'")
    private Map<String, DropRecord> detail;
}
