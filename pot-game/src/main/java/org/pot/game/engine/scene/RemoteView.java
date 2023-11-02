package org.pot.game.engine.scene;

import com.google.protobuf.Message;
import org.pot.message.protocol.world.WorldPointInfo;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class RemoteView extends View {
    private final int fromServerId;
    private final Queue<Message> pending = new LinkedBlockingQueue<>();

    public RemoteView(long playerId, ViewManger viewManger, int serverId) {
        super(playerId, viewManger);
        this.fromServerId = serverId;
    }

    public void notifyAdd(WorldPoint worldPoint) {
        notifyAdd(worldPoint.toWorldPointInfo().build());
    }

    public void notifyAdd(WorldPointInfo worldPointInfo) {
        if (!contains(worldPointInfo.getPoint().getX(), worldPointInfo.getPoint().getY())) {
            return;
        }
    }
}
