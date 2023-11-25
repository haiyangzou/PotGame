package org.pot.game.engine.world;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.relect.ConstructorUtil;
import org.pot.game.engine.world.module.instance.InstanceModule;
import org.pot.game.engine.world.module.map.WorldMapModule;
import org.pot.game.engine.world.module.monster.WorldMonsterModule;
import org.pot.game.engine.world.module.rally.WorldRallyModule;
import org.pot.game.engine.world.module.resource.WorldResourceModule;
import org.pot.game.engine.world.module.var.WorldVarModule;

@Slf4j
public enum WorldModuleType {
    WORLD_VAR(WorldVarModule.class),
    WORLD_MAP(WorldMapModule.class),
    WORLD_RALLY(WorldRallyModule.class),
    WORLD_MONSTER(WorldMonsterModule.class),
    WORLD_RESOURCE(WorldResourceModule.class),
    INSTANCE(InstanceModule.class),
    ;
    private final WorldModule module;

    <T extends WorldModule> WorldModuleType(Class<T> moduleClass) {
        this.module = ModuleFactory.instanceOf(moduleClass);
    }

    public static WorldModule[] modules() {
        WorldModuleType[] worldModuleTypes = WorldModuleType.values();
        WorldModule[] modules = new WorldModule[worldModuleTypes.length];
        for (int i = 0; i < modules.length; i++) {
            modules[i] = worldModuleTypes[i].module;
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

    public <T extends WorldModule> T getModule() {
        return (T) module;
    }
}
