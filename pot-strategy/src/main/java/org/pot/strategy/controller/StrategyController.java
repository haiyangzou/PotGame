package org.pot.strategy.controller;

import com.maxmind.geoip2.record.Country;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.http.HttpServletUtils;
import org.pot.common.util.GeoIpUtil;
import org.pot.common.util.JsonUtil;
import org.pot.strategy.service.StrategyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Controller
public class StrategyController {
    @Resource
    private StrategyService strategyService;

    @ResponseBody
    @RequestMapping(value = "/strategy", method = {RequestMethod.GET, RequestMethod.POST})
    public String strategy(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "version") String resVersion,
                           @RequestParam(value = "deviceOS") String deviceOS,
                           @RequestParam(value = "account", required = false, defaultValue = "") String account,
                           @RequestParam(value = "appVersion", required = false, defaultValue = "") String appVersion,
                           @RequestParam(value = "deviceName", required = false, defaultValue = "") String deviceName,
                           @RequestParam(value = "appPackageName", required = false, defaultValue = "") String appPackageName) {
        long time = System.currentTimeMillis();
        String requestIp = HttpServletUtils.getRequestIp(request);
        Map<String, Object> map = new HashMap<>();
        map.put("request_ip", requestIp);
        map.put("request_account", account);
        map.put("request_device_os", deviceOS);
        map.put("request_device_name", deviceName);
        map.put("request_res_version", resVersion);
        map.put("request_app_version", appVersion);
        map.put("request_app_package_name", appPackageName);
        Country country = GeoIpUtil.getCountry(requestIp);
        map.put("country_name", country == null ? StringUtils.EMPTY : country.getName());
        map.put("country_code", country == null ? StringUtils.EMPTY : country.getIsoCode());
        strategyService.putStrategyInfo(map, requestIp, deviceOS, deviceName, resVersion, appVersion, appPackageName);
        String result = JsonUtil.toJson(map);
        return result;
    }
}
