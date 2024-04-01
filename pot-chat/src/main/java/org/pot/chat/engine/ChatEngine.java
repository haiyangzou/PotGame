package org.pot.chat.engine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.player.PlayerCaches;
import org.pot.chat.module.ChatServerManager;
import org.pot.common.Constants;
import org.pot.common.communication.server.DefinedServerSupplier;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.engine.IHttpServer;
import org.pot.core.net.netty.FrameMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;
import org.pot.common.communication.server.ServerType;
import org.pot.dal.redis.ReactiveRedis;
import org.pot.remote.thrift.client.manager.RpcClientManager;
import org.pot.remote.thrift.server.RemoteServer;
import org.pot.web.JettyHttpServer;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatEngine extends AppEngine<ChatEngineConfig> {
    private volatile ServerId serverId;

    private RemoteServer remoteServer;
    private volatile IHttpServer httpServer;
    @Getter
    private final ChatServerManager chatServerManager = new ChatServerManager();

    public static ChatEngine getInstance() {
        return (ChatEngine) EngineInstance.getInstance();
    }

    public ChatEngine() throws Exception {
        super(ChatEngineConfig.class);
    }

    protected ChatEngine(Class<ChatEngineConfig> configClass) throws Exception {
        super(configClass);
    }

    @Override
    protected void doStart() throws Throwable {
        requestChatServerInfo();
        initChatManager();
        remoteServer = new RemoteServer(getConfig().getRemoteServerConfig());
        remoteServer.addServerTypeHandlerIfAbsent(ServerType.CHAT_SERVER, serverId.serverType);
        RpcClientManager.instance.open(serverId, remoteServer);
        remoteServer.start();
        initPlayerCaches();
        initHttpServer();
    }

    private void initPlayerCaches() {
        PlayerCaches.init(ChatEngine.getInstance().getConfig().getPlayerCacheConfig(), ReactiveRedis.global(), null, null);
    }

    @Override
    protected void doStop() {
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> getAsyncExecutor().isIdle());
        ReactiveRedis.close();
        remoteServer.stop();
        RpcClientManager.instance.shutdown();
        httpServer.shutdown();
    }

    @Override
    public IHttpServer getHttpServer() {
        return null;
    }

    @Override
    public NettyClientEngine<? extends FrameMessage> getNettyClientEngine() {
        return null;
    }

    @Override
    public NettyServerEngine<? extends FrameMessage> getNettyServerEngine() {
        return null;
    }


    private void requestChatServerInfo() throws Exception {
        int port = getConfig().getPort();
        int httpPort = getConfig().getJettyConfig().getPort();
        int rpcPort = getConfig().getRemoteServerConfig().getPort();
        String url = getConfig().getGlobalServerConfig().getServerInfoUrl();
        Server server = DefinedServerSupplier.getServerInfo(url, ServerType.CHAT_SERVER, port, httpPort, rpcPort);
        this.serverId = ServerId.of(server.getTypeId(), server.getServerId());

    }

    private void initHttpServer() {
        log.info("initialize http server ...");
        this.httpServer = new JettyHttpServer((getConfig()).getJettyConfig());
        this.httpServer.startup();
    }

    private void initChatManager() {
        this.chatServerManager.init();
    }
}
