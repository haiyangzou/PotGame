package org.pot.game.engine.scene;

import org.pot.game.engine.march.March;

public abstract class MarchRegulation {
    protected final AbstractScene scene;

    public MarchRegulation(AbstractScene scene) {
        this.scene = scene;
    }

    public abstract AbstractScene getScene();

    public double getMarchSpeedScale() {
        return 1;
    }

    public abstract void save(boolean async);

    public abstract void init();

    public abstract void tick();

    public abstract void onMarchAdd(March march);

    public abstract void onMarchRemove(March march);

    public abstract void onMarchUpdate(March march);
}