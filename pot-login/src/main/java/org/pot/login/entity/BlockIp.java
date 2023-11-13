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
public class BlockIp implements Serializable {
    private String blockIp;
    private Long relieveTime;
    private Date createTime;
    private Date updateTime;

    public BlockIp(String blockIp, Long relieveTime) {
        this.blockIp = blockIp;
        this.relieveTime = relieveTime;
    }
}
