package org.pot.game.resource.parameter;

import java.util.function.Function;

public interface GameParameters {
    Function<String, Parameter> SUPPLIER = k -> {
        Parameter p = ParameterConfig.getInstance().getSpec(k);
        return p == null ? StringParameterConfig.getInstance().getSpec(k) : p;
    };
    ParameterValue black_soil_marching_speed_coefficient = ParameterValue.of(SUPPLIER, "black_soil_marching_speed_coefficient");
}
