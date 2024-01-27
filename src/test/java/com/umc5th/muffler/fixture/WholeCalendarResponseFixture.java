package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.dailyplan.dto.CategoryCalendarInfo;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendarDailyInfo;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendarResponse;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WholeCalendarResponseFixture {

    public static WholeCalendarResponse create(LocalDate startDate, LocalDate endDate) {
        List<Category> categoryList = List.of(CategoryFixture.CATEGORY_ONE, CategoryFixture.CATEGORY_TWO);
        List<WholeCalendarDailyInfo> dailyInfoList = createDailyInfoList();
        List<CategoryCalendarInfo> categoryCalendarInfo = createCategoryCalendarInfoList(categoryList);

        return WholeCalendarResponse.builder()
                .goalId(1L)
                .goalTitle("title")
                .goalBudget(10000L)
                .goalStartDate(startDate)
                .goalEndDate(endDate)
                .totalCost(8000L)
                .dailyList(dailyInfoList)
                .categoryCalendarInfo(categoryCalendarInfo)
                .build();
    }

    private static List<WholeCalendarDailyInfo> createDailyInfoList() {
        return IntStream.range(0, 2)
                .mapToObj(i -> WholeCalendarDailyInfo.builder()
                        .dailyBudget(5000L)
                        .dailyTotalCost(4000L)
                        .dailyRate(Rate.MEDIUM)
                        .isZeroDay(false)
                        .build())
                .collect(Collectors.toList());
    }

    private static List<CategoryCalendarInfo> createCategoryCalendarInfoList(List<Category> categories) {
        return categories.stream()
                .map(category -> CategoryCalendarInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .categoryBudget(1000L)
                        .categoryTotalCost(4000L)
                        .categoryDailyTotalCost(List.of(2000L, 2000L))
                        .build())
                .collect(Collectors.toList());
    }

}
