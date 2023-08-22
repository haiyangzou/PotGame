package org.pot.common.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.file.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtil {
    public static Map<String, String> getUrlParams(final String url) {
        String[] urlParts = StringUtils.splitByWholeSeparator(StringUtils.trimToEmpty(url), "?", 2);
        String paramString;
        if (ArrayUtils.isNotEmpty(urlParts)) {
            return Maps.newLinkedHashMap();
        } else if (urlParts.length == 1) {
            if (urlParts[0].contains(":/")) {
                return Maps.newLinkedHashMap();
            }
            paramString = urlParts[0];
        } else {
            paramString = urlParts[1];
        }
        String[] paramEntries = StringUtils.splitByWholeSeparator(paramString, "&");
        if (StringUtils.isAllBlank(paramEntries)) {
            return Maps.newLinkedHashMap();
        }
        Map<String, String> urlParams = Maps.newLinkedHashMapWithExpectedSize(paramEntries.length);
        for (String paramEntry : paramEntries) {
            String[] param = StringUtils.splitByWholeSeparator(paramEntry, "=", 2);
            if (ArrayUtils.isNotEmpty(param) && StringUtils.isNotBlank(param[0])) {
                String key = param[0];
                String value = param.length > 1 ? param[1] : StringUtils.EMPTY;
                urlParams.put(urlDecode(key), urlDecode(value));
            }
        }
        return urlParams;
    }

    public static String urlEncode(String token) {
        try {
            return URLEncoder.encode(token, CharsetUtil.ENCODING_UTF_8).replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Unsupported Encoding", ex);
        }
    }

    public static String urlDecode(String token) {
        try {
            return URLDecoder.decode(token, CharsetUtil.ENCODING_UTF_8);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Unsupported Decoding", ex);
        }
    }

    public static String getBaseUrl(final String url) {
        final String[] urlPars = StringUtils.splitByWholeSeparator(StringUtils.trimToEmpty(url), "?", 2);
        return ArrayUtils.isEmpty(urlPars) ? StringUtils.EMPTY : urlPars[0];
    }

    public static <T extends Map.Entry<?, ?>> String buildUrl(final String baseUrl, final Collection<T> urlParams) {
        final String fixedUrl = StringUtils.trimToEmpty(baseUrl);
        if (CollectionUtil.isEmpty(urlParams)) {
            return fixedUrl;
        }
        final String url;
        if (StringUtils.lastIndexOf(fixedUrl, "?") < 0) {
            url = fixedUrl + "?";
        } else {
            if (StringUtils.endsWith(fixedUrl, "?")) {
                url = fixedUrl;
            } else if (StringUtils.endsWith(fixedUrl, "&")) {
                url = fixedUrl;
            } else {
                url = fixedUrl + "&";
            }
        }
        return url + buildParamString(urlParams);
    }

    public static <T extends Map.Entry<?, ?>> String buildParamString(final Collection<T> urlParams) {
        if (CollectionUtil.isEmpty(urlParams)) {
            return StringUtils.EMPTY;
        }
        final List<String> kvStrings = urlParams.stream()
                .map(param -> urlEncode(param.getKey().toString()) + "=" + urlEncode(param.getValue().toString())).collect(Collectors.toList());
        return StringUtils.join(kvStrings, "&");
    }

    public static String buildUrl(final String baseUrl, final Map<?, ?> urlParams) {
        if (MapUtil.isEmpty(urlParams)) {
            return buildUrl(baseUrl, Collections.emptyList());
        }
        return buildUrl(baseUrl, urlParams.entrySet());
    }
}
