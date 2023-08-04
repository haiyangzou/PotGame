package org.pot.login.domain.object;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_account")
@Getter
@Setter
public class UserAccount implements Serializable {
	@Id
	private Long uid;
	private String account;
	private String device;
	private Integer gmFlag;
	private Integer banFlag;
	private Integer guidePolicy;
	private Integer accountBind;
	private String facebookId;
	private String googlePlayerId;
	private String gameCenterId;
	private String email;
	private String appId;
	private String appName;
	private String appVersion;
	private String appPackageName;
	private String channel;
	private String platform;
	private String country;
	private String language;
	private String ip;
	private String network;
	private String phoneModel;
	private String deviceInfo;
	private String deviceOs;
	private String osVersion;
	private String appsFlyerId;
	private String advertisingId;
	private Date createTime;
	private Date updateTime;
}
