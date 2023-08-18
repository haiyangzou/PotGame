package org.pot.game.engine.scene;

public interface PointListener {
    void onPointAdded(WorldPoint worldPoint);

    void onPointRemoved(WorldPoint worldPoint);
}
