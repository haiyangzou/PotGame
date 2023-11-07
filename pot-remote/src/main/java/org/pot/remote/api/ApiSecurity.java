package org.pot.remote.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pot.common.http.HttpSecurity;
import org.pot.common.text.Jackson;
import org.pot.common.util.JsonUtil;

import java.io.IOException;
import java.util.TreeMap;

@Slf4j
public class ApiSecurity {
    private static final String _api = "_api";

    public static Jackson access(String url, String api, TreeMap<String, String> params) throws IOException {
        params.put(_api, api);
        String responseBody = HttpSecurity.doPostWithJsonBody(url, params);
        return new Jackson(responseBody);
    }

    public static ApiResponse execute(TreeMap<String, String> parameters) {
        String api = parameters.get(_api);
        try {
            if (!HttpSecurity.validate(parameters)) {
                return new ApiResponse(false, "api auth failed");
            } else if (StringUtils.isBlank(api)) {
                return new ApiResponse(false, "api name invalid");
            } else {
                return ApiSupport.execute(api, new Jackson(JsonUtil.toJson(parameters)));
            }
        } catch (Throwable t) {
            return new ApiResponse(false, ExceptionUtils.getStackTrace(ExceptionUtils.getRootCause(t)));
        }
    }

}
