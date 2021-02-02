package org.pot.core.netty.http;

import org.pot.core.script.ScriptManager;
import org.pot.core.service.HttpService;

public class ClusterHttpServerHandler extends HttpServerIoHandler {

  private HttpService service;

  public ClusterHttpServerHandler() {

  }

  protected HttpService getHttpService() {
    return service;
  }

  public void setHttpService(HttpService httpService) {
    this.service = service;
  }

  public void setScriptService(ScriptManager scriptService) {
    this.scriptService = scriptService;
  }
}
