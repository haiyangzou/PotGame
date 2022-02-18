package org.pot.room.service;

import org.pot.core.service.RoomService;

public class DefaultRoomService implements RoomService {

    @Override
    public String getRoom() {
        return "room1";
    }
}
