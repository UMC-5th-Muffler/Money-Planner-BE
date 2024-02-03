package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.LocalDate;
import java.util.ArrayList;

public class RoutineFixture {
    public static Routine ROUTINE_PER_ONE_WEEK(Member member, Category category, LocalDate startDate, LocalDate endDate) {
        return Routine.builder()
                .type(RoutineType.WEEKLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_ONE_WEEK")
                .cost(100L)
                .weeklyTerm(1)
                .category(category)
                .member(member)
                .weeklyRepeatDays(new ArrayList<>())
                .build();
    }

    public static Routine ROUTINE_PER_TWO_WEEK(Member member, Category category, LocalDate startDate, LocalDate endDate) {
        return Routine.builder()
                .type(RoutineType.WEEKLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_TWO_WEEK")
                .cost(100L)
                .weeklyTerm(2)
                .category(category)
                .member(member)
                .weeklyRepeatDays(new ArrayList<>())
                .build();
    }

    public static Routine ROUTINE_PER_THREE_WEEK(Member member, Category category, LocalDate startDate, LocalDate endDate) {
        return Routine.builder()
                .type(RoutineType.WEEKLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_THREE_WEEK")
                .cost(100L)
                .weeklyTerm(3)
                .category(category)
                .member(member)
                .weeklyRepeatDays(new ArrayList<>())
                .build();
    }

    public static Routine ROUTINE_PER_MONTH(Member member, Category category, LocalDate startDate, LocalDate endDate, Integer day) {
        return Routine.builder()
                .type(RoutineType.MONTHLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_MONTH")
                .cost(100L)
                .category(category)
                .member(member)
                .monthlyRepeatDay(day)
                .build();
    }
}
