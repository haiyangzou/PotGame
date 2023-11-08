package org.pot.game.engine.scene;

public abstract class MarchRegulation {
    protected final AbstractScene scene;

    public MarchRegulation(AbstractScene scene) {
        this.scene = scene;
    }

    public abstract AbstractScene getScene();

    public abstract void tick();

    public double getMarchSpeedScale() {
        return 1;
    }
}
