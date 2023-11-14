package org.pot.common.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.Constants;
import org.pot.common.cipher.SecurityUtil;
import org.pot.common.id.UniqueIdUtil;
import org.pot.common.net.http.OkHttp;
import org.pot.common.performance.memory.alloc.StringBuilderAlloc;
import org.pot.common.util.CollectionUtil;
import org.pot.common.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpSecurity {
    private static final String _salt = "_salt";
    private static final String _time = "_time";
    private static final String _sign = "_sign";
    private static final long timeout = TimeUnit.MINUTES.toMillis(10);
    private static final Map<String, Long> validateSalts = new ConcurrentHashMap<>();

    static {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    long now = System.currentTimeMillis();
                    ArrayList<String> removes = new ArrayList<>();
                    for (Map.Entry<String, Long> entry : validateSalts.entrySet()) {
                        String salt = entry.getKey();
                        Long time = entry.getValue();
                        if (time == null || now - time > timeout) {
                            removes.add(salt);
                        }
                        if (CollectionUtil.isNotEmpty(removes)) {
                            removes.forEach(validateSalts::remove);
                            log.info("evict validated size={}", removes.size());
                        }
                    }
                } catch (Throwable throwable) {
                    log.error("evict validated salt task", throwable);
                }
            }
        });
        thread.setName(HttpSecurity.class.getSimpleName());
        thread.setDaemon(true);
        thread.start();
    }

    public static boolean validate(TreeMap<String, String> parameters) {
        if (Constants.Env.isDebug()) return true;
        if (parameters == null) return false;
        String salt = parameters.get(_salt);
        if (StringUtils.isBlank(salt)) return false;
        String time = parameters.get(_time);
        if (StringUtils.isBlank(time)) return false;
        String sign = parameters.get(_sign);
        if (StringUtils.isBlank(sign)) return false;
        long now = System.currentTimeMillis();
        if (Math.abs(now - NumberUtils.toLong(time)) > timeout) return false;
        if (validateSalts.putIfAbsent(salt, now) != null) return false;
        TreeMap<String, String> withoutSignParameters = new TreeMap<>(parameters);
        withoutSignParameters.remove(_sign);
        return HttpSignature.validate(sign, withoutSignParameters, SecurityUtil::signRsa, HttpSecurity::getSignatureSource);
    }

    public static HttpResult postWithJsonBody(String url, TreeMap<String, String> parameters) throws IOException {
        HttpResult result = JsonUtil.parseJson(doPostWithJsonBody(url, parameters), HttpResult.class);
        return result;
    }

    public static String doPostWithJsonBody(String url, TreeMap<String, String> parameters) throws IOException {
        String body = OkHttp.COMMON.postWithJsonBody(url, inject(parameters));
        return body;
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
