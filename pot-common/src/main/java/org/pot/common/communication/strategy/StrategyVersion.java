package org.pot.common.communication.strategy;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.AppVersionUtil;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class StrategyVersion implements Serializable, Comparable<StrategyVersion> {
    private static final long serialVersionUID = 1L;
    private String version;
    private String packageName;
    private Integer examine;
    private Integer preView;
    private Integer upgrade;
    private String examineResourceUrl;
    private String examineGatewayHost;
    private String examineGatewayPort;
    private Date createTime;
    private Date updateTime;

    public boolean isMatchPackageName(String packageName) {
        if (StringUtils.isEmpty(this.packageName)) {
            return true;
        } else {
            return this.packageName.equals(packageName);
        }
    }

    @Override
    public int compareTo(StrategyVersion o) {
        return AppVersionUtil.compare(this.version, o.version);
    }
}
