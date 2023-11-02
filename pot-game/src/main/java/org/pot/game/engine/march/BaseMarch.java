package org.pot.game.engine.march;

public abstract class BaseMarch implements March {
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
