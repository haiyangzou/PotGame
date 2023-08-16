package org.pot.core.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class NewDay {
    public interface Task {
        void onNew();
    }

    private static int CUSTOM_ZERO;
    private static LocalDateTime DAY_ZERO_DATETIME;
    private static LocalDateTime WEEK_ZERO_DATETIME;
    private static LocalDateTime MONTH_ZERO_DATETIME;
    private static List<Task> dayTasks = new CopyOnWriteArrayList<>();
    private static List<Task> weekTasks = new CopyOnWriteArrayList<>();
    private static List<Task> monthTasks = new CopyOnWriteArrayList<>();

    public static void calculate() {
        if (DAY_ZERO_DATETIME == null) {
            DAY_ZERO_DATETIME = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(CUSTOM_ZERO, 0));
            DAY_ZERO_DATETIME = DAY_ZERO_DATETIME.isAfter(LocalDateTime.now()) ? DAY_ZERO_DATETIME.minusDays(1) : DAY_ZERO_DATETIME;
            WEEK_ZERO_DATETIME = DAY_ZERO_DATETIME.minusDays(DAY_ZERO_DATETIME.getDayOfWeek().getValue() - 1);
            MONTH_ZERO_DATETIME = DAY_ZERO_DATETIME.minusDays(DAY_ZERO_DATETIME.getDayOfMonth() - 1);
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (now.toLocalDate().isAfter(DAY_ZERO_DATETIME.toLocalDate())) {
                if (now.getHour() == CUSTOM_ZERO) {
                    DAY_ZERO_DATETIME = LocalDateTime.of(now.toLocalDate(), LocalTime.of(CUSTOM_ZERO, 0));
                    onNewDay();
                    if (DAY_ZERO_DATETIME.getDayOfWeek().getValue() == 1) {
                        WEEK_ZERO_DATETIME = DAY_ZERO_DATETIME;
                        onNewWeekDay();
                    }
                    if (DAY_ZERO_DATETIME.getDayOfMonth() == 1) {
                        MONTH_ZERO_DATETIME = DAY_ZERO_DATETIME;
                        onNewMonthDay();
                    }
                }
            }
        }
    }

    private static void onNewMonthDay() {
        for (Task task : monthTasks) {
            try {
                task.onNew();
            } catch (Exception exception) {
                log.error("Month Task Error{}", task.getClass().getName(), exception);
            }
        }
    }

    private static void onNewWeekDay() {
        for (Task task : weekTasks) {
            try {
                task.onNew();
            } catch (Exception exception) {
                log.error("Week Task Error{}", task.getClass().getName(), exception);
            }
        }
    }

    private static void onNewDay() {
        for (Task task : dayTasks) {
            try {
                task.onNew();
            } catch (Exception exception) {
                log.error("Day Task Error{}", task.getClass().getName(), exception);
            }
        }
    }

    public static void addDayTask(Task task) {
        dayTasks.add(task);
    }

    public static void addWeekTask(Task task) {
        dayTasks.add(task);
    }

    public static void addMonthTask(Task task) {
        dayTasks.add(task);
    }
}
