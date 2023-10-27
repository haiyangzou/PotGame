package org.pot.web.service;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.http.HttpServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

@Slf4j
public abstract class BaseHttpService extends HttpServlet {
    protected void setTextPlainContentType(HttpServletResponse resp) {
        resp.setContentType("text/plain; charset=utf-8");
    }

    protected void writeMessage(HttpServletResponse resp, String message) throws IOException {
        resp.getWriter().write(message);
    }

    protected TreeMap<String, String> getParameters(HttpServletRequest req) throws Exception {
        return HttpServletUtils.getParameters(req);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    protected abstract void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
