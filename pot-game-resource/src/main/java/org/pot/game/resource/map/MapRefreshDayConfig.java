package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

import java.util.List;

public class MapRefreshDayConfig extends InitJsonConfig<MapRefreshDay> {
    public static MapRefreshDayConfig getInstance() {
        return GameConfigSupport.getConfig(MapRefreshDayConfig.class);
    }

    public static int getIdWithServerOpenDays(int openDays) {
        List<MapRefreshDay> list = getInstance().getSpecList();
        for (MapRefreshDay mapRefreshDay : list) {
            if (openDays == mapRefreshDay.getDay()) {
                return mapRefreshDay.getId();
            }
        }
        return list.get(list.size() - 1).getId();
    }

}
