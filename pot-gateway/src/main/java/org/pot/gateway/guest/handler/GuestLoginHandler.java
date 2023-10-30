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
import org.pot.message.protocol.ProtocolSupport;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.login.LoginReqC2S;

@Slf4j
public final class GuestLoginHandler extends GuestRequestHandler<LoginReqC2S> {

    public GuestLoginHandler(Class<LoginReqC2S> requestType) {
        super(requestType);
    }

    @Override
    protected boolean handleRequest(Guest guest, LoginReqC2S request) throws Exception {
        try {
            long time = System.currentTimeMillis();
            String ip = guest.getConnection().getRemoteHost();
            GatewayEngineConfig gatewayEngineConfig = GatewayEngine.getInstance().getConfig();
            String requestData = HttpSecurityCodec.encode(request.toByteArray());
            String loginServiceUrl = gatewayEngineConfig.getGlobalServerConfig().getUserLoginUrl();
            String url = UrlUtil.buildUrl(loginServiceUrl, MapUtil.newHashMap("ip", ip, "data", requestData));
            String responseData = OkHttp.COMMON.get(url);
            if (StringUtils.isBlank(responseData)) {
                throw new ServiceException(CommonErrorCode.LOGIN_FAIL);
            }
            byte[] responseBytes = HttpSecurityCodec.decode(responseData);
            LoginDataS2S loginDataS2S = LoginDataS2S.parseFrom(responseBytes);
            long used = System.currentTimeMillis() - time;
            if (loginDataS2S.getIsMaintain()) {
                GuestHandlerUtil.sendMaintainNotice(guest, loginDataS2S);
                loginFail(guest, CommonErrorCode.SERVER_MAINTAIN.getErrorCode());
                return false;
            }
            int errorCode = loginDataS2S.getErrorCOde();
            String errorMessage = loginDataS2S.getErrorMessage();
            if (errorCode > 0) {
                if (errorCode == CommonErrorCode.VERSION_LOW.getErrorCode()) {
                    GuestHandlerUtil.versionFail(guest, loginDataS2S);
                } else {
                    loginFail(guest, errorCode);
                }
                return false;
            }
            RemoteUser remoteUser = new RemoteUser(guest, loginDataS2S);
            boolean success = GatewayEngine.getInstance().getRemoteUserManager().addRemoteUser(remoteUser);

        } catch (Throwable throwable) {
            throw throwable;
        }
        return false;
    }

    private void loginFail(Guest guest, int errorCode) {
        guest.disconnect(ProtocolSupport.buildProtoErrorMsg(getRequestType(), errorCode));
    }
}
