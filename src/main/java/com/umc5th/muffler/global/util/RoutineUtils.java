package com.umc5th.muffler.global.util;

import static com.umc5th.muffler.entity.constant.RoutineType.MONTHLY;
import static com.umc5th.muffler.entity.constant.RoutineType.WEEKLY;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.RoutineException;
import java.time.LocalDate;

public class RoutineUtils {

    private static final RoutineProcessor weeklyProcessor = new WeeklyRoutineProcessor();
    private static final RoutineProcessor monthlyProcessor = new MonthlyRoutineProcessor();

    public static boolean isRoutineDay(LocalDate date, Routine routine) {
        RoutineProcessor processor = getProcessorForRoutineType(routine);
        return processor.isRoutineDay(date, routine);
    }

    public static RoutineProcessor getProcessorForRoutineType(Routine routine) {
        if (routine.getType() == WEEKLY) {
            return weeklyProcessor;
        }
        if (routine.getType() == MONTHLY) {
            return monthlyProcessor;
        }
        throw new RoutineException(ErrorCode.ROUTINE_TYPE_NOT_FOUND);
    }
}
