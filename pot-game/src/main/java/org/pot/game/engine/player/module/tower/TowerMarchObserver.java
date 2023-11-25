package org.pot.game.engine.player.module.tower;

import com.google.protobuf.Message;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.alarm.MarchAlarmReceiver;
import org.pot.game.engine.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TowerMarchObserver implements MarchAlarmReceiver {
    private final Player player;

    private final Map<String, TowerMarchInfo> towerMarchInfoMap = new ConcurrentHashMap<>();

    public TowerMarchObserver(Player player) {
        this.player = player;
    }

    @Override
    public void sendMessage(Message message) {
        player.sendMessage(message);
    }

    @Override
    public void alertCity(March march) {

    }

    @Override
    public void alertMarch(March selfMarch, March march) {

    }

    @Override
    public void relaxCity(March march) {

    }

    @Override
    public void relaxMarch(March selfMarch, March march) {

    }
}
