package org.pot.game.engine.world.module.var;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pot.common.util.JsonUtil;

public interface WorldVarDef<VarType> {
    String key();

    VarType value();

    VarType value(VarType defaultValue);

    void update(VarType var);

    default void delete() {
        WorldVarModule.getInstance().remove(key());
    }

    enum BooleanVar implements WorldVarDef<Boolean> {
        TempleBattleActing,
        TempleBattleAutoDecreeOpen,
        ;
        private final String key = this.getClass().getSimpleName() + "#" + this.name();


        @Override
        public String key() {
            return key;
        }

        @Override
        public Boolean value() {
            return WorldVarModule.getInstance().getBoolean(key());
        }

        @Override
        public Boolean value(Boolean defaultValue) {
            return WorldVarModule.getInstance().getBoolean(key(), defaultValue);
        }

        @Override
        public void update(Boolean var) {
            WorldVarModule.getInstance().putBoolean(key(), var);
        }

        public boolean get(boolean v) {
            return value(v);
        }

        public void set(boolean v) {
            update(v);
        }
    }

    enum StringVar implements WorldVarDef<String> {
        PlayerBornRulePhaseInfo,
        BandResourceRefreshInfo,
        BlockMonsterRefreshInfo,
        BlockRallyRefreshInfo,
        TimeoutMonsterInfo,
        TimeoutRallyInfo,
        ;
        private final String key = this.getClass().getSimpleName() + "#" + this.name();


        @Override
        public String key() {
            return key;
        }

        @Override
        public String value() {
            return WorldVarModule.getInstance().getString(key());
        }

        @Override
        public String value(String defaultValue) {
            return WorldVarModule.getInstance().getString(key(), defaultValue);
        }

        @Override
        public void update(String var) {
            WorldVarModule.getInstance().putString(key(), var);
        }

        public String get(String v) {
            return value(v);
        }

        public void set(String v) {
            update(v);
        }

        public <T> T parseJson(Class<T> valueType) {
            return JsonUtil.parseJson(value(null), valueType);
        }

        public <T> T parseJson(TypeReference<T> valueType) {
            return JsonUtil.parseJson(value(null), valueType);
        }
    }

    enum IntVar implements WorldVarDef<Integer> {
        WorldMapCleanMin,
        WorldMapCleanMax,
        WorldMapCleanInterval,
        ;
        private final String key = this.getClass().getSimpleName() + "#" + this.name();


        @Override
        public String key() {
            return key;
        }

        @Override
        public Integer value() {
            return WorldVarModule.getInstance().getInt(key());
        }

        @Override
        public Integer value(Integer defaultValue) {
            return WorldVarModule.getInstance().getInt(key(), defaultValue);
        }

        @Override
        public void update(Integer var) {
            WorldVarModule.getInstance().putInt(key(), var);
        }

        public int get(int v) {
            return value(v);
        }

        public void set(int v) {
            update(v);
        }
    }

    enum LongVar implements WorldVarDef<Long> {
        BlackEarthResourceRefreshTime,
        ;
        private final String key = this.getClass().getSimpleName() + "#" + this.name();


        @Override
        public String key() {
            return key;
        }

        @Override
        public Long value() {
            return WorldVarModule.getInstance().getLong(key());
        }

        @Override
        public Long value(Long defaultValue) {
            return WorldVarModule.getInstance().getLong(key(), defaultValue);
        }

        @Override
        public void update(Long var) {
            WorldVarModule.getInstance().putLong(key(), var);
        }

        public long get(long v) {
            return value(v);
        }

        public void set(long v) {
            update(v);
        }
    }
}
