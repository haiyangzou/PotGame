package org.pot.game.engine.march.alarm;

import org.pot.core.net.protocol.MessageSender;
import org.pot.game.engine.march.March;

public interface MarchAlarmReceiver extends MessageSender {
    void alertCity(March march);

    void alertMarch(March selfMarch, March march);

    void relaxCity(March march);

    void relaxMarch(March selfMarch, March march);
}
