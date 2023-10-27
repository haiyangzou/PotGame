package org.pot.common.communication.strategy;

import lombok.Builder;
import lombok.Data;
import org.pot.common.util.AppVersionUtil;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class StrategyVersion implements Serializable, Comparable<StrategyVersion> {
    private static final long serialVersionUID = 1L;
    private String version;
    private Integer examine;
    private Integer preView;
    private Integer upgrade;
    private String examineResourceUrl;
    private String examineGatewayHost;
    private String examineGatewayPort;
    private Date createTime;
    private Date updateTime;

    @Override
    public int compareTo(StrategyVersion o) {
        return AppVersionUtil.compare(this.version, o.version);
    }
}
