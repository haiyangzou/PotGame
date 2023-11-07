package org.pot.login.entity;

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
public class RegisterPolicyGroup implements Serializable {
    private Integer id;
    private Integer policyId;
    private Integer groupId;
    private Integer ratio;
    private Date createTime;
    private Date updateTime;
}
