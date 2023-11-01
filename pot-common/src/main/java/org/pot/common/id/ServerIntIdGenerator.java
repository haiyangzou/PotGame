package org.pot.common.id;

import org.pot.common.concurrent.exception.ServiceException;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerIntIdGenerator implements IntIdGenerator {
    private final int serverId;
    private final int serverIdBits;
    private final int maxServerId;
    private final int idBits;
    private final int maxId;
    private final AtomicInteger generator;

    public ServerIntIdGenerator(int serverId) {
        this(serverId, 7);
    }

    public ServerIntIdGenerator(int serverId, int serverIdBits) {
        this.serverId = serverId;
        this.serverIdBits = serverIdBits;
        this.maxServerId = (1 << serverIdBits) - 1;
        this.idBits = MAX_BITS - serverIdBits;
        this.maxId = (serverId << idBits) - 1;
        this.generator = new AtomicInteger((serverId - 1) << idBits + 1);
    }

    public void initialize(int id) {
        generator.set(id);
    }

    @Override
    public int nextId() {
        int id = generator.getAndIncrement();
        if (id > maxId) {
            throw new ServiceException("no id available");
        }
        return id;
    }
}
