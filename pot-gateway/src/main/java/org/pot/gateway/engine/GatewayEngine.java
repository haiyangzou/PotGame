package org.pot.gateway.engine;

import lombok.Getter;
import org.pot.common.Constants;
import org.pot.common.communication.server.DefinedServerSupplier;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.core.AppEngine;
import org.pot.core.engine.EngineInstance;
import org.pot.core.engine.IHttpServer;
import org.pot.core.net.netty.*;
import org.pot.core.net.netty.websocket.WebCmdCodec;
import org.pot.core.net.netty.websocket.WebSocketServer;
import org.pot.gateway.guest.GuestWaitingRoom;
import org.pot.gateway.remote.RemoteServerManager;
import org.pot.gateway.remote.RemoteUserManager;
import org.pot.web.JettyHttpServer;
import org.pot.gateway.guest.GuestRequestHandlerSupport;

import java.util.concurrent.TimeUnit;

@Getter
public class GatewayEngine extends AppEngine<GatewayEngineConfig> {
    public static GatewayEngine getInstance() {
        return (GatewayEngine) EngineInstance.getInstance();
    }

    public GatewayEngine() throws Exception {
        super(GatewayEngineConfig.class);
    }

    private IHttpServer httpServer;
    private ServerId serverId;
    private NettyClientEngine<FramePlayerMessage> nettyClientEngine;
    private NettyServerEngine<FrameCmdMessage> nettyServerEngine;
    private WebSocketServer<FrameCmdMessage> webSocketServer;
    private RemoteUserManager remoteUserManager;
    private RemoteServerManager remoteServerManager;

    protected GatewayEngine(Class<GatewayEngineConfig> configClass) throws Exception {
        super(configClass);
    }

    @Override
    protected void doStart() throws Exception {
        GuestRequestHandlerSupport.init();
        initServerInfo();
        initNettyClientEngine();
        remoteUserManager = new RemoteUserManager();
        remoteServerManager = new RemoteServerManager();
        initNettyServerEngine();
        initHttpServer();
    }

    private void initHttpServer() {
        httpServer = new JettyHttpServer(getConfig().getJettyConfig());
        httpServer.startup();
    }

    private void initNettyClientEngine() {
        this.nettyClientEngine = new NettyClientEngine<>(getConfig(), FramePlayerCodec::new);
        this.nettyClientEngine.start();
    }

    private void initNettyServerEngine() {
        GuestWaitingRoom.getInstance().setAccept(true);
        addTicker(GuestWaitingRoom.getInstance());
        if (Constants.Env.isWeb()) {
            this.webSocketServer = new WebSocketServer<>(getConfig(), WebCmdCodec::new);
            this.webSocketServer.getConnectionManager().setListener(GuestWaitingRoom.getInstance());
            this.webSocketServer.start();
        } else {
            this.nettyServerEngine = new NettyServerEngine<>(getConfig(), FrameCmdCodec::new);
            this.nettyServerEngine.getConnectionManager().setListener(GuestWaitingRoom.getInstance());
            this.nettyServerEngine.start();
        }
    }

    private void initServerInfo() throws Exception {
        int port = getConfig().getPort();
        int httpPort = getConfig().getJettyConfig().getPort();
        int rpcPort = getConfig().getRemoteServerConfig().getPort();
        String url = getConfig().getGlobalServerConfig().getServerInfoUrl();
        Server server = DefinedServerSupplier.getServerInfo(url, ServerType.GATE_SERVER, port, httpPort, rpcPort);
        this.serverId = ServerId.of(server.getTypeId(), server.getServerId());
    }

    @Override
    protected void doStop() {
        GuestWaitingRoom.getInstance().setAccept(false);
        if (remoteServerManager != null) {
            remoteServerManager.close();
        }
        if (remoteUserManager != null) {
            remoteUserManager.close();
        }
        if (nettyServerEngine != null) {
            nettyServerEngine.stop();
        }
        if (nettyClientEngine != null) {
            nettyClientEngine.stop();
        }
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> getAsyncExecutor().isIdle());
    }

}
