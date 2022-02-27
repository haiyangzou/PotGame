package org.pot.room.service;

import org.pot.core.constant.GlobalProperties;
import org.pot.core.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultRoomService implements RoomService {
    @Autowired
    private GlobalProperties properties;
    @Override
    public String getRoom() {
        return "room"+properties.getProfile();
    }
}
