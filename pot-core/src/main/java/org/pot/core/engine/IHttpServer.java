package org.pot.core.engine;

import java.util.Map;

public interface IHttpServer {
    void start();

    void shutdown();

    int getRequests();

    int getRequestActive();

    Map<String, Object> getStatus();

    void onAcceptRequest(String requestURI, String requestIP);
}
