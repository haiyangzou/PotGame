package org.pot.game.engine;

import org.pot.common.task.PeriodTask;
import org.pot.common.util.DateTimeUnit;
import org.pot.core.AppEngine;
import org.pot.core.util.NewDay;
import org.pot.game.engine.rank.RankManager;

public class GamePeriodicTasks {
    private static final PeriodTask SECOND_TASK = new PeriodTask() {
        @Override
        public void doPeriodicTask() {
            NewDay.calculate();
        }

        @Override
        public DateTimeUnit getDateTimeUnit() {
            return DateTimeUnit.SECOND;
        }
    };

    public static void init(AppEngine<?> appEngine) {
        NewDay.calculate();
        appEngine.addPeriodicTask(SECOND_TASK);
        appEngine.addPeriodicTask(RankManager.getInstance());
    }
}
