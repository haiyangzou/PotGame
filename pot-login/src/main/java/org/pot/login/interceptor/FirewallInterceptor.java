package org.pot.login.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.http.HttpServletUtils;
import org.pot.common.net.ipv4.FireWall;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class FirewallInterceptor implements HandlerInterceptor {
    @Resource
    private FireWall webFirewall;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String requestIp = HttpServletUtils.getRequestIp(request);
        if (webFirewall.isDeniedIpv4(requestIp)) {
            log.error("id address not allowed");
            response.setContentType("text/plain; charset-utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("ip address not allowed");
            response.getWriter().flush();
            return false;
        }
        return true;
    }

}
