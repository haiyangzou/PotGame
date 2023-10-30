package org.pot.common.http;

import org.pot.common.cipher.SecurityUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.common.net.http.OkHttp;
import org.pot.common.performance.memory.alloc.StringBuilderAlloc;
import org.pot.common.util.JsonUtil;

import java.io.IOException;
import java.util.TreeMap;

public class HttpSecurity {
    private static final String _salt = "_salt";
    private static final String _time = "_time";
    private static final String _sign = "_sign";

    public static HttpResult postWithJsonBody(String url, TreeMap<String, String> parameters) throws IOException {
        return JsonUtil.parseJson(doPostWithJsonBody(url, parameters), HttpResult.class);
    }

    public static String doPostWithJsonBody(String url, TreeMap<String, String> parameters) throws IOException {
        return OkHttp.COMMON.postWithJsonBody(url, inject(parameters));
    }

    private static String getSignatureSource(TreeMap<String, String> paramters) {
        StringBuilder sb = StringBuilderAlloc.newSmallString();
        paramters.forEach((k, v) -> sb.append(k).append(v));
        return sb.toString();
    }

    public static TreeMap<String, String> inject(TreeMap<String, String> parameters) {
        if (parameters == null) {
            parameters = new TreeMap<>();
        }
        parameters.put(_salt, UniqueIdUtil.newUuid());
        parameters.put(_time, String.valueOf(System.currentTimeMillis()));
        parameters.put(_sign, HttpSignature.generate(parameters, SecurityUtil::signRsa, HttpSecurity::getSignatureSource));
        return parameters;
    }
}
