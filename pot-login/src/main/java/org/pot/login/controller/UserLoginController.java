package org.pot.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.pot.login.cache.GameServerDaoCache;
import org.pot.login.service.LoginNoticeService;
import org.pot.login.service.UserLoginService;
import org.pot.login.service.UserTokenService;
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
    private ReactiveStringRedisTemplate redisTemplate;

    @ResponseBody
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(value = "ip") String userIp,
                        @RequestParam(value = "data") String data) {
        return "";
    }
}
