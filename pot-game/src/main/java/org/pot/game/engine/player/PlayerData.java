package org.pot.game.engine.player;

import lombok.Getter;
import lombok.Setter;
import org.pot.common.function.Operation;
import org.pot.dal.async.IAsyncDbTask;
import org.pot.game.persistence.GameDb;

import java.io.Serializable;
import java.util.function.Consumer;

@Getter
@Setter
public class PlayerData implements Serializable {
    private volatile long uid;
    private volatile boolean loadOver = false;

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

    public void asyncLoad(Consumer<PlayerData> onSuccess, Consumer<PlayerData> onFail) {
        GameDb.local().submit(new IAsyncDbTask() {
            @Override
            public long getId() {
                return uid;
            }

            @Override
            public void execute() {
                load(onSuccess, onFail);
            }
        });
    }

    public void asyncInsert(Operation onSuccess, Operation onFail) {

    }

    public void insert(Operation onSuccess, Operation onFail) {

    }

    public void asyncDelete(Operation onSuccess, Operation onFail) {

    }

    public void load(Consumer<PlayerData> onSuccess, Consumer<PlayerData> onFail) {

    }

}
