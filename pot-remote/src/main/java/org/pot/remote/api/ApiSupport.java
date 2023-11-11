package org.pot.remote.api;

import com.google.common.collect.ImmutableSortedMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.pot.PotPackage;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.relect.ConstructorUtil;
import org.pot.common.text.Jackson;
import org.pot.common.util.ClassUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class ApiSupport {
    private static final Map<String, ApiMethod> apiMap;

    static {
        Map<String, ApiMethod> tempApiMap = new HashMap<>();
        try {
            Collection<Class<?>> allClazzSet = ClassUtil.getClasses(PotPackage.class.getPackage().getName());
            for (Class<?> clazz : allClazzSet) {
                if (!clazz.isAnnotationPresent(Api.class)) {
                    continue;
                }
                if (!ClassUtil.isConcrete(clazz)) {
                    continue;
                }
                if (!ConstructorUtil.containsNoneParamConstructor(clazz)) {
                    continue;
                }
                List<ApiMethod> apiMethodList = ApiSupport.getApiMethodList(clazz.newInstance());
                for (ApiMethod apiMethod : apiMethodList) {
                    String api = apiMethod.getDescription().getApi();
                    if (tempApiMap.containsKey(apiMethod.getDescription().getApi())) {
                        log.error("repeated api name:{}", api);
                    }
                    tempApiMap.put(api, apiMethod);
                }
            }

        } catch (Exception exception) {
            throw new IllegalStateException("init error", exception);
        }
        apiMap = ImmutableSortedMap.copyOf(tempApiMap, String::compareTo);
    }

    public static ApiResponse execute(String api, Jackson params) throws Exception {
        ApiMethod apiMethod = ApiSupport.getApiMethod(api);
        if (apiMethod == null) {
            throw new ServiceException("api" + api + "not exists");
        }
        return apiMethod.invoke(params);
    }

    public static ApiMethod getApiMethod(String api) {
        return StringUtils.isBlank(api) ? null : apiMap.get(api);
    }

    private static List<ApiMethod> getApiMethodList(Object object) {
        List<Method> apiMethods = MethodUtils.getMethodsListWithAnnotation(object.getClass(), Api.class);
        if (apiMethods.isEmpty()) {
            return Collections.emptyList();
        }
        List<ApiMethod> list = new ArrayList<>(apiMethods.size());
        for (Method api : apiMethods) {
            ApiDescription desc = getApiDescription(api);
            list.add(new ApiMethod(object, api, desc));
        }
        return list;
    }

    private static ApiDescription getApiDescription(Method api) {
        ApiDescription desc = new ApiDescription();
        String apiPrefix = api.getDeclaringClass().getSimpleName();
        Api apiClass = api.getDeclaringClass().getAnnotation(Api.class);
        if (StringUtils.isNotBlank(apiClass.value())) {
            apiPrefix = apiClass.value();
        }
        String apiName = api.getName();
        Api apiMethod = api.getAnnotation(Api.class);
        if (StringUtils.isNotBlank(apiMethod.value())) {
            apiName = apiMethod.value();
        }
        desc.setComment(apiMethod.comment());
        desc.setApi(apiPrefix + "." + apiName);
        desc.setReturnType(api.getReturnType().getSimpleName());

        for (Parameter param : api.getParameters()) {
            String name = param.getName();
            ApiParam apiParam = param.getAnnotation(ApiParam.class);
            if (apiParam != null && StringUtils.isNotBlank(apiParam.value())) {
                name = apiParam.value();
            }
            desc.addParamType(name, param.getType());
        }
        return desc;
    }
}
