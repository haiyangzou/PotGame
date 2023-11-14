package org.pot.common.net.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.Util;
import okhttp3.internal.http.StatusLine;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.ShutdownHook;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.net.TrustAny;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.UrlUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttp {
    private static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");
    public static final OkHttp COMMON = new OkHttp();
    private final OkHttpClient okHttpClient;

    static {
        ShutdownHook.registerShutdownHook(COMMON::destroy);
    }

    public OkHttp() {
        this(300, 300, 15, 15, 15);
    }

    public OkHttp(int maxIdle, int keepAlive, int connectTimeout, int writeTimeout, int readTimeout) {
        this(maxIdle, keepAlive, connectTimeout, writeTimeout, readTimeout, TimeUnit.SECONDS);
    }

    public OkHttp(int maxIdle, int keepAlive, int connectTimeout, int writeTimeout, int readTimeout, TimeUnit unit) {
        this(null, maxIdle, keepAlive, connectTimeout, writeTimeout, readTimeout, unit);
    }

    public OkHttp(ExecutorService executorService, int maxIdle, int keepAlive, int connectTimeout, int writeTimeout, int readTimeout, TimeUnit unit) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (executorService == null) {
            int minThreads = ThreadUtil.AVAILABLE_PROCESSORS / 2;
            int maxThreads = ThreadUtil.AVAILABLE_PROCESSORS * 2;
            executorService = new ThreadPoolExecutor(minThreads, maxThreads, keepAlive, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        builder.dispatcher(new Dispatcher(executorService));
        this.okHttpClient = builder
                .connectTimeout(connectTimeout, unit)
                .writeTimeout(writeTimeout, unit)
                .readTimeout(readTimeout, unit)
                .connectionPool(new ConnectionPool(maxIdle, keepAlive, unit))
                .connectionSpecs(Arrays.asList(ConnectionSpec.RESTRICTED_TLS, ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                .sslSocketFactory(TrustAny.TRUST_ANY_SSL_SOCKET_FACTORY, TrustAny.TRUST_ANY_MANAGER)
                .hostnameVerifier(TrustAny.TRUST_ANY_HOSTNAME_VERIFIER)
                .build();
    }

    public void destroy() throws IOException {
        Cache cache = this.okHttpClient.cache();
        if (cache != null) {
            cache.close();
        }
        this.okHttpClient.dispatcher().executorService().shutdown();
        this.okHttpClient.connectionPool().evictAll();
    }

    private String getRedirectUrl(String url, Response response) {
        if (response.code() == StatusLine.HTTP_TEMP_REDIRECT || response.code() == StatusLine.HTTP_PERM_REDIRECT) {
            String location = response.headers().get("Location");
            String newBaseUrl = UrlUtil.getBaseUrl(location);
            String oldBaseUrl = UrlUtil.getBaseUrl(url);
            return StringUtils.replaceOnce(url, oldBaseUrl, newBaseUrl);
        }
        return null;
    }

    private void ensureSuccessful(String url, Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(url + "response not successful " + response);
        }
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String redirectUrl = getRedirectUrl(url, response);
            if (redirectUrl != null) {
                return get(redirectUrl);
            }
            ensureSuccessful(url, response);
            return Objects.requireNonNull(response.body()).toString();
        } catch (IOException e) {
            log.error(url, e);
            throw e;
        }
    }

    public String postWithJsonBody(String url, Object jsonObject) throws IOException {
        String body = postWithJsonBody(url, Collections.emptyMap(), JsonUtil.toJackSon(jsonObject));
        return body;
    }

    public String postWithJsonBody(String url, Map<String, String> headMap, Object jsonObject) throws IOException {
        return postWithJsonBody(url, headMap, JsonUtil.toJackSon(jsonObject));
    }

    public String postWithJsonBody(String url, Map<String, String> headMap, String jsonBody) throws IOException {
        Headers.Builder headBuilder = new Headers.Builder();
        if (headMap != null) {
            headMap.forEach(headBuilder::add);
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(headBuilder.build())
                .post(RequestBody.create(JSON, jsonBody))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String redirectUrl = getRedirectUrl(url, response);
            if (redirectUrl != null) {
                return postWithJsonBody(redirectUrl, headMap, jsonBody);
            }
            ensureSuccessful(url, response);
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            log.error(url, e);
            throw e;
        }
    }
}
