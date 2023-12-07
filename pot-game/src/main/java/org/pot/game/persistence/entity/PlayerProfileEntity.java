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
    @Column(name = "reg_time")
    private volatile long regTime;
    @Column(name = "reg_device_os")
    private volatile String regDeviceOs;
    @Column(name = "login_ip")
    private volatile String loginIp;
    @Column(name = "login_device_os")
    private volatile String loginDeviceOs;
    @Column(name = "last_login_time")
    private volatile long lastLoginTime;
    @Column(name = "last_online_time")
    private volatile long lastOnlineTime;
    @Column(name = "online_time")
    private volatile long onlineTime;
    @Builder.Default
    @Column(name = "world_point_id")
    private volatile int worldPointId = -1;
    @Column(name = "world_clean_time")
    private volatile long worldCleanTime;
    @Column(name = "pay_first_time")
    private volatile long payFirstTime;
    @Column(name = "pay_total_price")
    private volatile double payTotalPrice = 0D;
    @Column(name = "pay_total_diamond")
    private volatile long payTotalDiamond;
    @Column(name = "app_version")
    private volatile String appVersion;
    @Column(name = "os_version")
    private volatile String osVersion;
    @Column(name = "channel")
    private volatile String channel;
    @Column(name = "platform")
    private volatile String platform;
    @Column(name = "country")
    private volatile String country;
    @Column(name = "language")
    private volatile String language;
    @Column(name = "cid")
    private volatile String cid;
    @Column(name = "chat_ban")
    private volatile Long chatBan;
    @Column(name = "rename_ban")
    private volatile Long renameBan;
    @Column(name = "translate_language")
    private volatile String translateLanguage;
}
