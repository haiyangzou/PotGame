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
}
