package org.pot.core.netty.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.pot.core.util.JsonUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zou
 * @mail 1
 */
@Data
@Component
@ConfigurationProperties("netty")
public class NettyProperties {

  /**
   * 服务器注册心跳
   */
  public static final int ServerRegisterHeart = 2;
  /**
   * 服务器配置
   * <p>
   * netty.serverConfigs[0].name=cluster-tcp<br> netty.serverConfigs[0].port=8100
   * netty.serverConfigs[1].name=cluster-http<br> netty.serverConfigs[1].port=8200
   * </p>
   */
  private List<NettyServerConfig> serverConfigs = new ArrayList<>();
  /**
   * 客户端配置
   */
  public List<NettyClientConfig> clientConfigs = new ArrayList<>();

  public List<NettyServerConfig> getServerConfigs() {
    return serverConfigs;
  }

  public void setServerConfigs(List<NettyServerConfig> serverConfigs) {
    this.serverConfigs = serverConfigs;
  }

  public List<NettyClientConfig> getClientConfigs() {
    return clientConfigs;
  }

  public void setClientConfigs(List<NettyClientConfig> clientConfigs) {
    this.clientConfigs = clientConfigs;
  }


  @Override
  public String toString() {
    return JsonUtils.toJSON(this);
  }
}
