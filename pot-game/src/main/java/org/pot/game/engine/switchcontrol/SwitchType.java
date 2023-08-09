package org.pot.game.engine.switchcontrol;

public enum SwitchType {
    ONCE,
    CIRCLE,
    ;

    public static boolean isCircle(String str) {
        return CIRCLE.name().equals(str);
    }

    public static boolean isOnce(String str) {
        return ONCE.name().equals(str);
    }
}
