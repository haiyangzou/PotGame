package org.pot.game.engine.world.module.var;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WorldVarModule extends AbstractWorldModule {
    public static WorldVarModule singleton() {
        return WorldModuleType.WORLD_VAR.getModule();
    }

    private final RunSignal saveSignal = new RunSignal(TimeUnit.MINUTES.toMillis(1));
    private final Map<String, String> varMap = new ConcurrentHashMap<>();
    private volatile boolean changed = false;

    @Override
    public void init() {

    }

    @Override
    public void tick() {
        if (saveSignal.signal() || changed) {
            asyncSave();
        }
    }

    private void asyncSave() {

    }

    public void remove(String key) {
        varMap.remove(key);
        changed = true;
    }

    public boolean containsKey(String key) {
        return varMap.containsKey(key);
    }

    public boolean getBoolean(String key) {
        return BooleanUtils.toBoolean(varMap.get(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return containsKey(key) ? getBoolean(key) : defaultValue;
    }

    public void putBoolean(String key, boolean value) {
        varMap.put(key, Boolean.toString(value));
        changed = true;
    }

    public String getString(String key) {
        return Objects.requireNonNull(varMap.get(key));
    }

    public String getString(String key, String defaultValue) {
        return containsKey(key) ? getString(key) : defaultValue;
    }

    public void putString(String key, String value) {
        varMap.put(key, StringUtils.trimToEmpty(value));
        changed = true;
    }

    public int getInt(String key) {
        return Integer.parseInt(varMap.get(key));
    }

    public int getInt(String key, int defaultValue) {
        return containsKey(key) ? getInt(key) : defaultValue;
    }

    public void putInt(String key, int value) {
        varMap.put(key, Integer.toString(value));
        changed = true;
    }

    public long getLong(String key) {
        return Long.parseLong(varMap.get(key));
    }

    public long getLong(String key, long defaultValue) {
        return containsKey(key) ? getLong(key) : defaultValue;
    }

    public void putLong(String key, long value) {
        varMap.put(key, Long.toString(value));
        changed = true;
    }
}
