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
public class RegisterGroupLocale implements Serializable {
    private Integer id;
    private Integer groupId;
    private Integer localeId;
    private Integer ratio;
    private Date createTime;
    private Date updateTime;
}
