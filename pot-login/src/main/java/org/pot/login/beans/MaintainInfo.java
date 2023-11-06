package org.pot.login.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class MaintainInfo {
    private String maintainNoticeTitle = StringUtils.EMPTY;
    private String maintainNoticeDetail = StringUtils.EMPTY;
    private int maintainRemainingTime;
}
