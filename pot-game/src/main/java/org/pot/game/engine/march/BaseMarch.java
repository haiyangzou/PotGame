package org.pot.game.engine.march;

import org.pot.game.engine.enums.MarchType;

public abstract class BaseMarch implements March {
    public BaseMarch(MarchManager marchManager, MarchType type, int sourcePoint, int targetPoint) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getOwnerId() {
        return 0;
    }

    @Override
    public void onAdd(MarchManager marchManager) {

    }

    @Override
    public void onRemove(MarchManager marchManager) {

    }

    @Override
    public MarchManager getManager() {
        return null;
    }

    @Override
    public void onInitPlayerData() {

    }

    @Override
    public int getTargetPoint() {
        return 0;
    }
}
