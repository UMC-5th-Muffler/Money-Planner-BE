package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.constant.RoutineType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoutineFixture {

    public static final Routine ROUTINE_ONE = Routine.builder()
            .id(1L)
            .type(RoutineType.MONTHLY)
            .startDate(LocalDate.of(2024, 1, 1))
            .title("루틴1")
            .memo("루틴루틴")
            .cost(1000L)
            .category(CategoryFixture.CATEGORY_ONE)
            .monthlyRepeatDay(1)
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
                        .monthlyRepeatDay(i)
                        .build())
                .collect(Collectors.toList());
    }
}
