package org.pot.login.domain.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements Serializable {
    private Long uid;
    private Long accountUid;
    private Integer serverId;
    private Integer banFlag;
    private Long banEndTime;
    private Long lastLoginTime;
    private Date createTime;
    private Date updateTime;
}
