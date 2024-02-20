package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.constant.MonthlyRepeatType;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoutineFixture {
    public static Routine routinePerWeek(Member member, Category category, LocalDate startDate, LocalDate endDate) {
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

    public static Routine routinePerTwoWeek(Member member, Category category, LocalDate startDate, LocalDate endDate) {
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

    public static Routine routinePerThreeWeek(Member member, Category category, LocalDate startDate, LocalDate endDate) {
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

    public static Routine routinePerMonth(Member member, Category category, LocalDate startDate, LocalDate endDate, Integer day) {
        return Routine.builder()
                .type(RoutineType.MONTHLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_MONTH")
                .cost(100L)
                .category(category)
                .member(member)
                .monthlyRepeatType(MonthlyRepeatType.SPECIFIC_DAY_OF_MONTH)
                .specificDay(day)
                .build();
    }
    public static Routine routineLastDayOfMonth(Member member, Category category, LocalDate startDate, LocalDate endDate) {
        return Routine.builder()
                .type(RoutineType.MONTHLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_LAST_DAY_OF_MONTH")
                .cost(100L)
                .category(category)
                .member(member)
                .monthlyRepeatType(MonthlyRepeatType.LAST_DAY_OF_MONTH)
                .build();
    }
    public static Routine routineFirstDayOfMonth(Member member, Category category, LocalDate startDate, LocalDate endDate) {
        return Routine.builder()
                .type(RoutineType.MONTHLY)
                .startDate(startDate)
                .endDate(endDate)
                .memo("memo")
                .title("ROUTINE_PER_FIRST_DAY_OF_MONTH")
                .cost(100L)
                .category(category)
                .member(member)
                .monthlyRepeatType(MonthlyRepeatType.FIRST_DAY_OF_MONTH)
                .build();
    }

    public static final Routine ROUTINE_ONE = Routine.builder()
            .id(1L)
            .type(RoutineType.MONTHLY)
            .startDate(LocalDate.of(2024, 1, 1))
            .title("루틴1")
            .memo("루틴루틴")
            .cost(1000L)
            .category(CategoryFixture.CATEGORY_ONE)
            .specificDay(1)
            .build();

    public static final Routine ROUTINE_TWO = Routine.builder()
            .id(2L)
            .type(RoutineType.WEEKLY)
            .startDate(LocalDate.of(2024, 1, 1))
            .title("루틴2")
            .cost(1000L)
            .category(CategoryFixture.CATEGORY_ONE)
            .build();

    public static final Routine ROUTINE_THREE = Routine.builder()
            .id(1L)
            .type(RoutineType.MONTHLY)
            .startDate(LocalDate.of(2024, 1, 1))
            .title("루틴1")
            .memo("루틴루틴")
            .cost(1000L)
            .category(CategoryFixture.CATEGORY_ONE)
            .monthlyRepeatType(MonthlyRepeatType.FIRST_DAY_OF_MONTH)
            .build();

    public static List<Routine> createList(int num, LocalDate date) {

        return IntStream.rangeClosed(0, num)
                .mapToObj(i -> Routine.builder()
                        .id((long) i)
                        .type(RoutineType.MONTHLY)
                        .startDate(date.plusDays(i))
                        .title("루틴")
                        .memo("memo")
                        .cost(1000L)
                        .member(MemberFixture.MEMBER_ONE)
                        .category(CategoryFixture.CATEGORY_ONE)
                        .monthlyRepeatType(MonthlyRepeatType.FIRST_DAY_OF_MONTH)
                        .build())
                .collect(Collectors.toList());
    }
}
