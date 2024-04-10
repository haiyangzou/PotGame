package org.pot.game.resource.parameter;

import java.util.function.Function;

public interface GameParameters {
    Function<String, Parameter> SUPPLIER = k -> {
        Parameter p = ParameterConfig.getInstance().getSpec(k);
        return p == null ? StringParameterConfig.getInstance().getSpec(k) : p;
    };
    ParameterValue black_soil_marching_speed_coefficient = ParameterValue.of(SUPPLIER, "black_soil_marching_speed_coefficient");
    ParameterValue channel_max = ParameterValue.of(SUPPLIER, "channel_max");
    ParameterValue worldTalk_cd = ParameterValue.of(SUPPLIER, "worldTalk_cd");
    ParameterValue worldTalk_same_cd = ParameterValue.of(SUPPLIER, "worldTalk_same_cd");
    ParameterValue worldTalk_id = ParameterValue.of(SUPPLIER, "worldTalk_id");
    ParameterValue privateChat_max = ParameterValue.of(SUPPLIER, "privateChat_max");
    ParameterValue bookMark_Default = ParameterValue.of(SUPPLIER, "bookMark_Default");
    ParameterValue energy_limit = ParameterValue.of(SUPPLIER, "energy_limit");
    ParameterValue recovery_energy_time = ParameterValue.of(SUPPLIER, "recovery_energy_time");
    ParameterValue power_limit = ParameterValue.of(SUPPLIER, "power_limit");
    ParameterValue recovery_power_time = ParameterValue.of(SUPPLIER, "recovery_power_time");
}
