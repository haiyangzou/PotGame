package org.pot.web;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.Servlet;

public class DetectedHttpService {
    final Class<? extends Servlet> type;
    final String path;
    final boolean ignoreRequestLog;

    public DetectedHttpService(Class<? extends Servlet> type, EnableHttpService annotation) {
        this.type = type;
        this.path = StringUtils.strip(annotation.path());
        this.ignoreRequestLog = annotation.ignoreRequestLog();
    }
}
