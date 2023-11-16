package org.pot.game.engine.world.module.instance;

import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceModule extends AbstractWorldModule {
    private final Map<Long, Instance> instanceMap = new ConcurrentHashMap<>();

    public InstanceModule() {
    }

    @Override
    public void init() {
//        for (InstanceType instanceType : InstanceType.values()) {
//            instanceType.getInitOperation().operate();
//        }
        //加載數據庫中的副本數據
    }

    public static InstanceModule singleton() {
        return WorldModuleType.INSTANCE.getModule();
    }

    public Instance getInstanceForParticipant(long playerId) {
        for (Instance instance : instanceMap.values()) {
            if (instance.isParticipant(playerId)) {
                return instance;
            }
        }
        return null;
    }
}
