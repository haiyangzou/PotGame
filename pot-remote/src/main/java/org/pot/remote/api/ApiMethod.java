package org.pot.remote.api;

import lombok.Getter;
import org.pot.common.text.Jackson;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;

@Getter
public class ApiMethod {
    private final Object object;
    private final Method method;
    private final ApiDescription description;
    private final FastClass fastClass;
    private final FastMethod fastMethod;

    public ApiMethod(Object object, Method method, ApiDescription description) {
        this.object = object;
        this.method = method;
        this.description = description;
        this.fastClass = FastClass.create(object.getClass());
        this.fastMethod = fastClass.getMethod(method.getName(), method.getParameterTypes());
    }

    public ApiResponse invoke(Jackson params) throws Exception {
        Object[] args = getMethodParams(params);
        Object result = fastMethod.invoke(object, args);
        if (result instanceof ApiResponse) {
            return (ApiResponse) result;
        }
        return new ApiResponse(true, result);
    }

    private Object[] getMethodParams(Jackson params) {
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            ParameterDesc desc = description.getParamDesc(i);
            args[i] = params.get(desc.getName(), desc.getType());
        }
        return args;
    }
}
