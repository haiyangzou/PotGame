package org.pot.web.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pot.common.http.HttpServletUtils;
import org.pot.core.engine.EngineInstance;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

@Slf4j
public abstract class BaseHttpService extends HttpServlet {
    protected boolean isDeniedIpv4(String ip) {
        return EngineInstance.getConfig().getWebFirewall().isDeniedIpv4(ip);
    }

    protected void setTextPlainContentType(HttpServletResponse resp) {
        resp.setContentType("text/plain; charset=utf-8");
    }

    protected void writeMessage(HttpServletResponse resp, String message) throws IOException {
        resp.getWriter().write(message);
    }

    protected TreeMap<String, String> getParameters(HttpServletRequest req) throws Exception {
        return HttpServletUtils.getParameters(req);
    }

    protected boolean firewall() {
        return true;
    }

    protected void setTextHtmlContextType(HttpServletResponse resp) {
        resp.setContentType("text/html; charset=utf-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String requestIP = HttpServletUtils.getRequestIp(req);
        EngineInstance.getHttpServer().onAcceptRequest(requestURI, requestIP);
        if (firewall() && isDeniedIpv4(requestIP)) {
            setTextPlainContentType(resp);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("ip address not allowed");
            resp.getWriter().flush();
            return;
        }
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            setTextHtmlContextType(resp);
            doService(req, resp);
        } catch (Exception e) {
            log.error("process servlet request error,requestURI={},requestIP={}", requestURI, requestIP, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(ExceptionUtils.getStackTrace(e));
        }
        resp.getWriter().flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected abstract void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
