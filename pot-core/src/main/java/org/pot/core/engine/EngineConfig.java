package org.pot.core.engine;

import lombok.Getter;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.Jetty;
import org.pot.common.Constants;
import org.pot.common.config.*;
import org.pot.common.net.ipv4.FireWall;
import org.pot.common.relect.ConstructorUtil;
import org.pot.core.Launcher;
import org.pot.core.config.NettyConfig;

import java.io.File;

@Getter
public class EngineConfig extends NettyConfig {
    private static final Configurations configurations = new Configurations();
    private ExecutorConfig asyExecutorConfig;
    private FireWall tcpFirewall = FireWall.empty();
    private FireWall webFirewall = FireWall.lan();
    private DbConfig localDbConfig;
    private GlobalServerConfig globalServerConfig;
    private RedisConfig localRedisConfig;
    private RedisConfig globalRedisConfig;
    private RedisConfig rankRedisConfig;
    private JettyConfig jettyConfig;
    private RemoteServerConfig remoteServerConfig;

    public static <T extends EngineConfig> T loadConfiguration(Class<T> clazz) throws Exception {
        File propertiesFile = new File(Launcher.Env.getConfigFile());
        CompositeConfiguration configuration = new CompositeConfiguration();
        configuration.addConfiguration(configurations.properties(propertiesFile));
        configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        T gateEngineConfig = ConstructorUtil.newObjectWithNonParam(clazz);
        gateEngineConfig.setProperties(configuration);
        return gateEngineConfig;
    }

    @Override
    protected void setProperties(Configuration config) {
        super.setProperties(config);
        this.localDbConfig = DbConfig.loadDbConfig("local", config);
        this.asyExecutorConfig = ExecutorConfig.loadExecutorConfig("engine", config);
        this.jettyConfig = JettyConfig.loadJettyConfig(config);
        this.remoteServerConfig = RemoteServerConfig.loadRemoteConfig(config);
        this.globalServerConfig = GlobalServerConfig.loadGlobalServerConfig(config);
        this.localRedisConfig = RedisConfig.loadRedisConfig("local", config);
        this.globalRedisConfig = RedisConfig.loadRedisConfig("global", config);
        this.rankRedisConfig = RedisConfig.loadRedisConfig("rank", config);
        String tcpFirewallFile = config.getString("tcp.firewall.file", "");
        if (StringUtils.isNotBlank(tcpFirewallFile)) {
            tcpFirewall = FireWall.file(Constants.Env.getContextPath() + tcpFirewallFile);
        }
        String webFirewallFile = config.getString("web.firewall.file", "");
        if (StringUtils.isNotBlank(webFirewallFile)) {
            webFirewall = FireWall.file(Constants.Env.getContextPath() + webFirewallFile, FireWall.lan());
        }
    }
}
