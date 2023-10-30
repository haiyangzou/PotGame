package org.pot.gateway.guest.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.http.HttpSecurityCodec;
import org.pot.common.net.http.OkHttp;
import org.pot.common.util.MapUtil;
import org.pot.common.util.UrlUtil;
import org.pot.gateway.engine.GatewayEngine;
import org.pot.gateway.engine.GatewayEngineConfig;
import org.pot.gateway.guest.Guest;
import org.pot.gateway.guest.GuestRequestHandler;
import org.pot.gateway.remote.RemoteUser;
import org.pot.message.protocol.ProtocolPrinter;
import org.pot.message.protocol.ProtocolSupport;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.login.LoginReconnectC2S;

@Slf4j
public class GuestReconnectHandler extends GuestRequestHandler<LoginReconnectC2S> {
    @Override
    protected boolean handleRequest(Guest guest, LoginReconnectC2S request) throws Exception {
        try {
            long time = System.currentTimeMillis();
            String token = request.getTokenKey();
            GatewayEngineConfig gatewayEngineConfig = GatewayEngine.getInstance().getConfig();
            String reconnectServiceUrl = gatewayEngineConfig.getGlobalServerConfig().getUserReconnectUrl();
            String url = UrlUtil.buildUrl(reconnectServiceUrl, MapUtil.newHashMap("token", token));
            String responseData = OkHttp.COMMON.get(url);
            if (StringUtils.isBlank(responseData)) {
                reconnectFail(guest, CommonErrorCode.RECONNECT_FAIL.getErrorCode());
                return false;
            }

            byte[] responseBytes = HttpSecurityCodec.decode(responseData);
            LoginDataS2S loginDataS2S = LoginDataS2S.parseFrom(responseBytes);
            LoginDataS2S.Builder builder = loginDataS2S.toBuilder().setIsNewRole(false).setIsReconnect(true);
            long used = System.currentTimeMillis() - time;
            if (loginDataS2S.getIsMaintain()) {
                GuestHandlerUtil.sendMaintainNotice(guest, loginDataS2S);
                reconnectFail(guest, CommonErrorCode.SERVER_MAINTAIN.getErrorCode());
                return false;
            }
            int errorCode = loginDataS2S.getErrorCOde();
            String errorMessage = loginDataS2S.getErrorMessage();
            if (errorCode > 0) {
                if (errorCode == CommonErrorCode.VERSION_LOW.getErrorCode()) {
                    GuestHandlerUtil.versionFail(guest, loginDataS2S);
                } else {
                    reconnectFail(guest, errorCode);
                }
                return false;
            }
            RemoteUser remoteUser = new RemoteUser(guest, builder.build());
            boolean success = GatewayEngine.getInstance().getRemoteUserManager().addRemoteUser(remoteUser);
            if (!success) {
                log.error("Guest reconnect add remote user fail. account={},conn={},request={}", loginDataS2S.getLoginReqC2S().getAccount(), guest.getConnection(), ProtocolPrinter.printJson(request));
                throw new ServiceException(CommonErrorCode.RECONNECT_FAIL);
            }
        } catch (Throwable throwable) {
            reconnectFail(guest, CommonErrorCode.RECONNECT_FAIL.getErrorCode());
            throw throwable;
        }
        return false;
    }

    private void reconnectFail(Guest guest, int errorCode) {
        guest.disconnect(ProtocolSupport.buildProtoErrorMsg(getRequestType(), errorCode));
    }
}
