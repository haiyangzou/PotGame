package org.pot.gateway.guest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.pot.common.function.Ticker;
import org.pot.common.net.ipv4.FireWall;
import org.pot.gateway.engine.PotGateway;
import org.pot.gateway.net.connection.ConnectionManagerListener;
import org.pot.gateway.net.connection.IConnection;
import org.pot.gateway.net.netty.FrameCmdMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuestWaitingRoom implements Ticker, ConnectionManagerListener<FrameCmdMessage> {
    @Getter
    private static final GuestWaitingRoom instance = new GuestWaitingRoom();
    @Getter
    @Setter
    private volatile boolean accept = false;
    private final BlockingQueue<Guest> waitiGuests = new LinkedBlockingQueue<>();

    boolean addWaitingGuest(Guest guest) {
        return waitiGuests.offer(guest);
    }

    @Override
    public boolean onAdd(IConnection<FrameCmdMessage> connection) throws Exception {
        boolean tempAccept = accept;
        if (!tempAccept) {
            return false;
        }
        FireWall fireWall = FireWall.file("firewall");
        if (fireWall != null && fireWall.isDeniedIpv4(connection.getRemoteHost())) {
            log.info("GuestWaitingRoom firewall reject connection,conn={}", connection);
            return false;
        }

        return addWaitingGuest(new Guest(connection));

    }

    @Override
    public void onRemove(IConnection<FrameCmdMessage> connection) throws Exception {

    }

    @Override
    public void tick() {
        int size = waitiGuests.size();
        Guest guest;
        while (size-- > 0 && (guest = waitiGuests.poll()) != null) {
            if (guest.ensureValid()) {
                asyncExecuteGuestTask(guest);
            } else {
                log.info("Guest Leave,conn={}", guest.getConnection());
            }

        }
    }

    private void asyncExecuteGuestTask(Guest guest) {
        PotGateway.getInstance().getAsyncExecutor().execute(new GuestTask(guest));
    }

}
