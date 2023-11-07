package org.pot.remote.api;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApiDescription {
    private String api;
    private String comment;
    private String returnType;
    private final List<ParameterDesc> paramList = new ArrayList<>();

    void addParamType(String paramName, Class<?> type) {
        paramList.add(new ParameterDesc(paramName, type));
    }

    ParameterDesc getParamDesc(int index) {
        return index >= paramList.size() ? null : paramList.get(index);
    }
}
