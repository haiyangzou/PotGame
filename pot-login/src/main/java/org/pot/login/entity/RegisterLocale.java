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
public class RegisterLocale {
    private Integer id;
    private String name;
    private String inclusiveCountryIsoCodes;
    private String exclusiveCountryIsoCodes;
    private String inclusiveLanguages;
    private String exclusiveLanguages;
    private Date createTime;
    private Date updateTime;
}
