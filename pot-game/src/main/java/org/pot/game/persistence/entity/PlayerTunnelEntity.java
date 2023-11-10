package org.pot.game.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "playerId")
@Table(name = "player_tunnel")
public class PlayerTunnelEntity implements Serializable {
    @Id
    @Getter
    @Setter
    @Column(name = "player_id")
    private Long playerId;
    @Getter
    @Setter
    @Column(name = "player_data")
    private String playerData;
    @Getter
    @Setter
    @Column(name = "visa_data")
    private String visaData;
    @Getter
    @Setter
    @Column(name = "state")
    private String state;

    @Getter
    @Setter
    @Column(name = "source_server_type_id")
    private Integer sourceServerTypeId;

    @Getter
    @Setter
    @Column(name = "target_server_type_id")
    private Integer targetServerTypeId;

    @Getter
    @Setter
    @Column(name = "target_server_id")
    private Integer targetServerId;
    @Getter
    @Setter
    @Column(name = "source_server_id")
    private Integer sourceServerId;
}
