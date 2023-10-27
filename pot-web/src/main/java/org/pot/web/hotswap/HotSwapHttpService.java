package org.pot.web.hotswap;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.HotSwapUtil;
import org.pot.web.EnableHttpService;
import org.pot.web.service.BaseHttpService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@EnableHttpService(path = "/hotSwap")
public class HotSwapHttpService extends BaseHttpService {
    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setTextPlainContentType(resp);
        Map<String, String> parameters = getParameters(req);
        String version = StringUtils.stripToEmpty(parameters.get("_version"));
        writeMessage(resp, HotSwapUtil.hotSwap(version));
    }
}
