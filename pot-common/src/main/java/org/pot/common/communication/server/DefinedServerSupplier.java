package org.pot.common.communication.server;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pot.common.Constants;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.http.HttpResult;
import org.pot.common.http.HttpSecurity;
import org.pot.common.http.HttpSecurityCodec;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.LogUtil;
import org.pot.common.util.MapUtil;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class DefinedServerSupplier {
    public static List<Server> getServerList(String url) throws Exception {
        TreeMap<String, String> params = MapUtil.newTreeMap(String::compareTo, "type", "");
        return getGenericServerList(url, params, new TypeReference<List<Server>>() {
        });
    }

    public static Server getServerInfo(String url, ServerType serverType, int port, int httpPort, int rpcPort) throws Exception {
        TreeMap<String, String> params = MapUtil.newTreeMap(String::compareTo, "type", String.valueOf(serverType.getId()));
        return getGenericServerInfo(url, port, httpPort, rpcPort, params, new TypeReference<Server>() {
        });
    }

    public static List<GameServer> getGameServerList(String url) throws Exception {
        return getGenericServerList(url, new TreeMap<>(String::compareTo), new TypeReference<List<GameServer>>() {
        });
    }

    private static <T> List<T> getGenericServerList(String url, TreeMap<String, String> params, TypeReference<List<T>> typeReference) throws Exception {
        params.put("time", String.valueOf(DateTimeUtil.getUnixTimestamp()));
        HttpResult httpResult = HttpSecurity.postWithJsonBody(url, params);
        if (!httpResult.isSuccess()) {
            throw new ServiceException(LogUtil.format("Get Server List Error. errorCode={},errorMessage={}", httpResult.getErrorCode(), httpResult.getErrorMessage()));
        }
        String string = HttpSecurityCodec.decodeString(httpResult.getStringData());
        List<T> servers = JsonUtil.parseJson(string, typeReference);
        return servers == null ? Collections.emptyList() : servers;
    }

    private static <T> T getGenericServerInfo(String url, int port, int httpPort, int rpcPort, TreeMap<String, String> params, TypeReference<T> typeReference) throws Exception {
        params.put("time", String.valueOf(DateTimeUtil.getUnixTimestamp()));
        params.put("host", Ipv4Util.join(Constants.Env.getLocalhostIp()));
        params.put("port", String.valueOf(port));
        params.put("httpPort", String.valueOf(httpPort));
        params.put("rpcPort", String.valueOf(rpcPort));
        HttpResult httpResult = HttpSecurity.postWithJsonBody(url, params);
        if (!httpResult.isSuccess()) {
            throw new ServiceException(LogUtil.format("Get Server List Error. errorCode={},errorMessage={}", httpResult.getErrorCode(), httpResult.getErrorMessage()));
        }
        String string = HttpSecurityCodec.decodeString(httpResult.getStringData());
        return JsonUtil.parseJson(string, typeReference);
    }
}
