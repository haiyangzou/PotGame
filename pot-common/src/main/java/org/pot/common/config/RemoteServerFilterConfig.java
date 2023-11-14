package org.pot.common.config;

import org.pot.common.communication.server.ServerType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemoteServerFilterConfig {
    private static Map<ServerType, Set<Integer>> serverType2connectServerIds = new HashMap<>();
    private static Map<ServerType, Set<Integer>> serverType2excludeConnectionServerIds = new HashMap<>();
}
