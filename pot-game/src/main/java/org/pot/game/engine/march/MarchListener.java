package org.pot.game.engine.march;

public interface MarchListener {
    void onMarchAdded(March march);

    void onMarchRemoved(March march);

    void onMarchUpdated(March march);
}
