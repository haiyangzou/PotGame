package org.pot.login.controller;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.communication.server.GameServer;
import org.pot.common.http.HttpResult;
import org.pot.common.http.HttpSecurity;
import org.pot.common.http.HttpSecurityCodec;
import org.pot.common.http.HttpServletUtils;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.util.JsonUtil;
import org.pot.login.beans.ServiceError;
import org.pot.login.cache.GameServerDaoCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("gameServer")
public class GameServerController {
    @Resource
    private GameServerDaoCache gameServerDaoCache;

    @ResponseBody
    @RequestMapping(value = "/getList", method = {RequestMethod.GET, RequestMethod.POST})
    public String getList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        TreeMap<String, String> parameters = HttpServletUtils.getParameters(request);
        HttpResult httpResult = new HttpResult(parameters);
        List<GameServer> servers = Collections.emptyList();
        try {
            if (HttpSecurity.validate(parameters)) {
                httpResult.setSuccess(true);
                servers = gameServerDaoCache.selectAll();
                httpResult.setData(HttpSecurityCodec.encodeString(JsonUtil.toJson(servers)));
            } else {
                ServiceError.SIGNATURE_ERROR.warp(httpResult);
            }
        } catch (Throwable e) {
            log.error("get game server list error", e);
            ServiceError.UNKNOWN_ERROR.warp(httpResult);
            httpResult.setErrorMessage(e.getMessage());
        }
        String result = JsonUtil.toJson(httpResult);
        String ip = HttpServletUtils.getRequestIp(request);
        long used = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        log.info("get game server list used ={}ms,ip={},result={}", used, ip, result);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        TreeMap<String, String> parameters = HttpServletUtils.getParameters(request);
        HttpResult httpResult = new HttpResult(parameters);
        GameServer server = null;
        String host = parameters.get("host");
        String port = parameters.get("port");
        String httpPort = parameters.get("httpPort");
        String rpcPort = parameters.get("rpcPort");
        try {
            if (StringUtils.isBlank(host) || !Ipv4Util.split(host).stream().allMatch(Ipv4Util::isIpv4Address)) {
                ServiceError.PARAMETER_ERROR.warp(httpResult);
            } else if (!NumberUtils.isParsable(port) || !NumberUtils.isParsable(httpPort) || !NumberUtils.isParsable(rpcPort)) {
                ServiceError.PARAMETER_ERROR.warp(httpResult);
            } else if (HttpSecurity.validate(parameters)) {
                server = findGameServer(host, Integer.parseInt(port), Integer.parseInt(httpPort), Integer.parseInt(rpcPort));
                if (server == null) {
                    ServiceError.SERVER_INFO_NOT_FOUND_ERROR.warp(httpResult);
                } else {
                    httpResult.setSuccess(true);
                    httpResult.setData(HttpSecurityCodec.encodeString(JsonUtil.toJson(server)));
                }
            } else {
                ServiceError.SIGNATURE_ERROR.warp(httpResult);
            }
        } catch (Throwable e) {
            log.error("get game server list error", e);
            ServiceError.UNKNOWN_ERROR.warp(httpResult);
            httpResult.setErrorMessage(e.getMessage());
        }
        String result = JsonUtil.toJson(httpResult);
        String ip = HttpServletUtils.getRequestIp(request);
        long used = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        log.info("get game server info used ={}ms,ip={},result={}", used, ip, result);
        return result;

    }

    private GameServer findGameServer(String host, int port, int httpPort, int rpcPort) {
        List<GameServer> results = gameServerDaoCache.selectAll().stream()
                .filter(s -> port == s.getPort() && httpPort == s.getHttpPort() && rpcPort == s.getRpcPort())
                .filter(s -> CollectionUtils.containsAll(Ipv4Util.split(host), Ipv4Util.split(s.getHost())))
                .collect(Collectors.toList());
        if (results.size() > 1) {
            log.error("Repeated GameServer Error");
            return null;
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        return null;
    }

}
