package org.pot.login.controller;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.http.HttpServletUtils;
import org.pot.common.util.JsonUtil;
import org.pot.remote.api.ApiResponse;
import org.pot.remote.api.ApiSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class ApiHttpController {
    @ResponseBody
    @RequestMapping(value = "/api", method = {RequestMethod.GET, RequestMethod.POST})
    public String doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        TreeMap<String, String> parameters = HttpServletUtils.getParameters(request);
        ApiResponse apiResponse = ApiSecurity.execute(parameters);
        String result = JsonUtil.toJson(apiResponse);
        long used = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        return result;
    }
}
