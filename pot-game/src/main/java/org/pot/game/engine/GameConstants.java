package org.pot.game.engine;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface GameConstants {
    long BAND_RESOURCE_REFRESH_MILLIS = TimeUnit.MILLISECONDS.toMillis(120);
    Map<Integer, Double> BAND_RESOURCE_PERCENTS = new ImmutableMap.Builder<Integer, Double>()
            .put(7, 0.0535)
            .put(6, 0.0365)
            .put(5, 0.0365)
            .put(4, 0.0343)
            .put(3, 0.0343)
            .put(2, 0.0402)
            .put(1, 0.0402)
            .build();
    //Block的诖误刷新时间间隔
    long BLOCK_MONSTER_REFRESH_MILLIS = TimeUnit.MINUTES.toMillis(15);
    //Block的诖误占Block的坐标数的比例,3.2%
    double BLOCK_MONSTER_PERCENT = 0.032;

    long BLOCK_RALLY_REFRESH_MILLIS = TimeUnit.MINUTES.toMillis(15);
    double BLOCK_RALLY_PERCENT = 0.044;
}
