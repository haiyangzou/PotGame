package org.pot.core.config;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.pot.core.util.JsonUtils;

/**
 * 服务配置
 *
 * @author zhy
 */
@JsonRootName("details")
public class ServiceConfig {

    /**
     * 服务器配置信息 json字符串
     */
    private String serverConfig;

    /**
     * 反序列化后的对象
     */
    private transient Object configObject;

    public String getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(String serverConfig) {
        this.serverConfig = serverConfig;
    }

    public <T> T getConfig(Class<T> tClass) {
        if (configObject == null) {
            configObject = JsonUtils.toT(serverConfig, tClass);
        }
        return (T) configObject;
    }

}
