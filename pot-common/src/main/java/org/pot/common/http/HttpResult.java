package org.pot.common.http;

import lombok.Getter;
import lombok.Setter;
import org.pot.common.util.JsonUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class HttpResult implements Serializable {
    @Getter
    private long timestamp = System.currentTimeMillis();
    @Getter
    @Setter
    private boolean success;
    @Getter
    private Map<String, String> parameters;
    @Getter
    @Setter
    private Integer errorCode;
    @Getter
    @Setter
    private String errorMessage;
    @Setter
    private Object data;

    public HttpResult() {
        this.parameters = new TreeMap<>();
    }

    public HttpResult(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public <T> T getData(Class<T> type) {
        return JsonUtil.parseJson(JsonUtil.toJson(data), type);
    }

    public String getStringData() {
        return getData(String.class);
    }

}
