package org.pot.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.server.GameServer;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.http.HttpSecurityCodec;
import org.pot.common.http.HttpServletUtils;
import org.pot.dal.redis.lock.RedisLock;
import org.pot.dal.redis.lock.RedisLockFactory;
import org.pot.login.beans.UserLoginInfo;
import org.pot.login.cache.GameServerDaoCache;
import org.pot.login.service.LoginNoticeService;
import org.pot.login.service.UserLoginService;
import org.pot.login.service.UserTokenService;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.login.LoginReqC2S;
import org.pot.message.protocol.login.NewServerInfoDto;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("user")
public class UserLoginController {
    @Resource
    private UserLoginService userLoginService;
    @Resource
    private UserTokenService userTokenService;
    @Resource
    private LoginNoticeService loginNoticeService;
    @Resource
    private GameServerDaoCache gameServerDaoCache;
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;

    @ResponseBody
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(value = "ip") String userIp,
                        @RequestParam(value = "data") String data) {
        String ip = HttpServletUtils.getRequestIp(request);
        UserLoginInfo userLoginInfo = null;
        LoginDataS2S loginDataS2S = null;
        String result = "";
        try {
            final LoginReqC2S loginReqC2S = LoginReqC2S.parseFrom(HttpSecurityCodec.decode(data));
            final int serverId = gameServerDaoCache.selectOne(loginReqC2S.getServerId()) == null ? 0 : loginReqC2S.getServerId();
            userLoginInfo = new UserLoginInfo(loginReqC2S, serverId, userIp);
            Throwable loginThrowable = null;
            final String account = userLoginInfo.getLoginReqC2S().getAccount();
            final RedisLock mutex = RedisLockFactory.getRedisLock(account, globalReactiveRedisTemplate);
            try {
                mutex.lock();
                userLoginService.loginUser(userLoginInfo);
            } catch (ServiceException ex) {
                userLoginInfo.setIErrorCode(ex.getErrorCode());
                userLoginInfo.setErrorMessage(ex.getMessage());
                loginThrowable = ex;
            } catch (Throwable ex) {
                userLoginInfo.setIErrorCode(CommonErrorCode.LOGIN_FAIL);
                userLoginInfo.setErrorMessage("user login occur an error");
                loginThrowable = ex;
            } finally {
                mutex.unlock();
            }
            LoginDataS2S.Builder builder = LoginDataS2S.newBuilder();
            builder.setLoginReqC2S(userLoginInfo.getLoginReqC2S());
            builder.setAccountUid(userLoginInfo.getAccountUid());
            builder.setGameUid(userLoginInfo.getGameUid());
            builder.setServerId(userLoginInfo.getServerId());
            builder.setIp(userLoginInfo.getIp());
            builder.setToken(userTokenService.generaTokenKey(userLoginInfo.getGameUid()));
            GameServer gameServer = gameServerDaoCache.getNewGameServer();
            if (gameServer != null) {
                NewServerInfoDto.Builder newServerInfo = NewServerInfoDto.newBuilder();
                newServerInfo.setServerId(gameServer.getServerId());
                newServerInfo.setOpenTime(gameServer.getOpenTimeStamp());
                builder.setNewServerInfo(newServerInfo.build());
            }
            loginDataS2S = builder.build();
            userTokenService.addTokenData(userLoginInfo.getGameUid(), loginDataS2S);
            result = HttpSecurityCodec.encode(loginDataS2S.toByteArray());
            if (loginThrowable != null) {
                throw loginThrowable;
            }
        } catch (Throwable throwable) {
            log.error("user login occur an error.");
        }

        return result;
    }
}
