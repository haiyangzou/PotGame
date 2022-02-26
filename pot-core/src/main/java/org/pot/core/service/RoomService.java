package org.pot.core.service;

import org.apache.dubbo.config.annotation.DubboReference;

public interface RoomService {
    @DubboReference(loadbalance = "roundrobin")
    String getRoom();
}
