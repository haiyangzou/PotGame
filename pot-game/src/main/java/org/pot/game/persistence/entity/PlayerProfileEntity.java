package org.pot.game.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_profile")
public class PlayerProfileEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "uid")
    private volatile long uid;
    @Column(name = "name")
    private volatile String name;
    @Column(name = "account")
    private volatile String account;
    @Column(name = "account_uid")
    private volatile long accountUid;
    @Column(name = "device")
    private volatile String device;
    @Column(name = "server_id")
    private volatile int serverId;
    @Column(name = "union_id")
    private volatile int unionId;
}
