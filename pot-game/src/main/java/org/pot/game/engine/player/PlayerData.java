package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.common.function.Operation;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.game.persistence.GameDb;

import java.io.Serializable;

@Getter
public class PlayerData implements Serializable {
    private volatile long uid;

    public PlayerData(long uid) {
        this.uid = uid;
    }

    public void asyncUpdate(Operation onSuccess, Operation onFail) {
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                update(onSuccess, onFail);
            }
        });
    }

    private void update(Operation onSuccess, Operation onFail) {

    }

}
