package org.pot.game.engine.world;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.relect.ConstructorUtil;
import org.pot.game.engine.world.module.map.WorldMapModule;
import org.pot.game.engine.world.module.var.WorldVarModule;

@Slf4j
public enum WorldModuleType {
    WORLD_VAR(WorldVarModule.class),
    WORLD_MAP(WorldMapModule.class),
    ;
    private final WorldModule instance;
    @Getter
    private final Class<? extends WorldModule> moduleClass;

    <T extends WorldModule> WorldModuleType(Class<T> moduleClass) {
        this.moduleClass = moduleClass;
        this.instance = ModuleFactory.instanceOf(moduleClass);
    }

    public <T extends WorldModule> T getInstance() {
        return (T) instance;
    }

    public static WorldModule[] modules() {
        WorldModuleType[] worldModuleTypes = WorldModuleType.values();
        WorldModule[] modules = new WorldModule[worldModuleTypes.length];
        for (int i = 0; i < modules.length; i++) {
            modules[i] = worldModuleTypes[i].instance;
        }
        return modules;
    }

    private static class ModuleFactory {
        private static <T extends WorldModule> T instanceOf(Class<T> moduleClass) {
            try {
                return ConstructorUtil.newObjectWithNonParam(moduleClass);
            } catch (Throwable cause) {
                log.error("Failed to create world module instance of type{}", moduleClass.getName(), cause);
                return null;
            }
        }
    }
}
