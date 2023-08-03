package org.pot.dal.async;

import org.pot.common.structure.ElapsedTimeMonitor;

public interface IAsyncDbTask {
    long getId();

    void execute();

    ElapsedTimeMonitor elapsedTimeMonitor = ElapsedTimeMonitor.ofDefaultWarm(IAsyncDbTask.class.getName(), "ms");
}
