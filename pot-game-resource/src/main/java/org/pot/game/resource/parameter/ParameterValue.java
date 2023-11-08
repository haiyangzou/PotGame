package org.pot.game.resource.parameter;

import java.util.function.Function;

public final class ParameterValue {
    private final Function<String, Parameter> supplier;
    private final String name;

    public ParameterValue(Function<String, Parameter> supplier, String name) {
        this.supplier = supplier;
        this.name = name;
    }

    public static ParameterValue of(Function<String, Parameter> supplier, String name) {
        return new ParameterValue(supplier, name);
    }

    private Parameter parameter() {
        return supplier.apply(name);
    }

    public double getDouble() {
        return Double.parseDouble(parameter().getValue());
    }

    public boolean exists() {
        return parameter() != null;
    }

    public double getDouble(double defaultValue) {
        return exists() ? getDouble() : defaultValue;
    }
}
