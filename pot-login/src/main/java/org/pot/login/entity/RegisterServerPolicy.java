package org.pot.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterServerPolicy {
    private Integer id;
    private Integer policyId;
    private Integer serverId;
    private Integer priority;
    private Date createTime;
    private Date updateTime;
}
