package org.pot.login.http;

import org.pot.core.netty.http.HttpServerIoHandler;
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
  
}
