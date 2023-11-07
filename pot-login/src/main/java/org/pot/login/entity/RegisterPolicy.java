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
public class RegisterPolicy implements Serializable {
    private Integer id;
    private String name;
    private Integer priority;
    private Integer disable;
    private Integer totalMaxCount;
    private Integer dayMaxCount;
    private Integer hourMaxCount;
    private String inclusiveCountryIsoCodes;
    private String exclusiveCountryIsoCodes;
    private String inclusiveLanguages;
    private String exclusiveLanguages;
    private Date createTime;
    private Date updateTime;
}
