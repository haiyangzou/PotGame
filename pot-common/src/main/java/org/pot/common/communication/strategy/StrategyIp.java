package org.pot.common.communication.strategy;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class StrategyIp implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;
    private Integer valid;
    private String resourceUrl;
    private String gatewayHost;
    private String gatewayPort;
    private String remark;
    private Date createTime;
    private Date updateTime;

}
