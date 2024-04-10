package org.pot.game.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.player.common.PlayerFavoritesInfo;
import org.pot.game.engine.player.common.PlayerFrame;
import org.pot.game.engine.player.common.PlayerIcon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "player_common")
public class PlayerCommonEntity {
    @Id
    @Column(name = "id", columnDefinition = "bigint NOT NULL COMMENT '")
    private Long id;

    @Column(name = "profile_icon_id", columnDefinition = "int NOT NULL DEFAULT '0' COMMENT '")
    private Integer profileIconId;

    @Column(name = "profile_frame_id", columnDefinition = "int NOT NULL DEFAULT '0' COMMENT '")
    private Integer profileFrameId;

    @Column(name = "profile_picture", columnDefinition = "blob COMMENT '")
    private byte[] profilePicture;

    @Column(name = "level", columnDefinition = "int NOT NULL DEFAULT '1' COMMENT '")
    private Integer level;

    @Column(name = "exp", columnDefinition = "int NOT NULL DEFAULT '0' COMMENT '")
    private Integer exp;

    @Column(name = "power_refresh_time", columnDefinition = "bigint NOT NULL COMMENT '")
    private Long powerRefreshTime;

    @Column(name = "energy_refresh_time", columnDefinition = "bigint NOT NULL COMMENT '")
    private Long energyRefreshTime;

    @Column(name = "picture_refresh_time", columnDefinition = "bigint NOT NULL COMMENT '")
    private Long pictureRefreshTime;

    @Column(name = "frame_detail", columnDefinition = "longtext COMMENT '")
    private List<PlayerFrame> frameDetail;

    @Column(name = "icon_detail", columnDefinition = "longtext COMMENT '")
    private List<PlayerIcon> iconDetail;

    @Column(name = "statistics_detail", columnDefinition = "longtext COMMENT '")
    private Map<String, String> statisticsDetail;

    @Column(name = "favorites_detail", columnDefinition = "longtext COMMENT '")
    private PlayerFavoritesInfo favoritesDetail;

    @Column(name = "forbidden_player", columnDefinition = "longtext COMMENT '")
    private Set<Long> forbiddenPlayer;

    @Column(name = "sys_mail_id", columnDefinition = "bigint COMMENT '")
    private long sendSysMailId;
}
