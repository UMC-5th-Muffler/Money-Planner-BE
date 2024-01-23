package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.homeDto.CategoryCalendarInfo;
import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarDailyInfo;
import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.expense.service.HomeService;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Test
    void 홈화면_조회() throws Exception {

        String memberId = "1";
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        LocalDate calendarDate = LocalDate.of(2024, 1, 1);

        Goal goal = GoalFixture.create();

        List<Long> dailyBudgetList = Arrays.asList(5000L, 5000L);
        List<Long> dailyTotalCostList = Arrays.asList(4000L, 4000L);
        List<Boolean> isZeroDayList = Arrays.asList(false, false);
        List<Category> categoryList = Arrays.asList(CategoryFixture.CATEGORY_ONE, CategoryFixture.CATEGORY_TWO);
        List<Long> categoryBudgetList = Arrays.asList(1000L, 1000L);
        List<Long> categoryTotalCostList = Arrays.asList(4000L, 4000L);

        long totalCost = dailyTotalCostList.stream().mapToLong(Long::valueOf).sum();

        List<WholeCalendarDailyInfo> dailyInfoList = IntStream.range(0, dailyBudgetList.size())
                .mapToObj(i -> WholeCalendarDailyInfo.builder()
                        .dailyBudget(dailyBudgetList.get(i))
                        .dailyTotalCost(dailyTotalCostList.get(i))
                        .dailyRate(Level.HIGH) // 임시
                        .isZeroDay(isZeroDayList.get(i))
                        .build())
                .collect(Collectors.toList());

        List<CategoryCalendarInfo> categoryCalendarInfo = IntStream.range(0, categoryList.size())
                .mapToObj(i -> {
                    Category category = categoryList.get(i);
                    Long categoryBudget = categoryBudgetList.get(i);
                    Long categoryTotalCost = categoryTotalCostList.get(i);

                    List<CategoryCalendarDailyInfo> dailyCategoryInfoList = IntStream.rangeClosed(0, 1)
                            .mapToObj(day -> new CategoryCalendarDailyInfo(2000L, Level.HIGH))
                            .collect(Collectors.toList());

                    return CategoryCalendarInfo.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .categoryBudget(categoryBudget)
                            .categoryTotalCost(categoryTotalCost)
                            .categoryGoalSummary(dailyCategoryInfoList)
                            .build();
                })
                .collect(Collectors.toList());

        WholeCalendarResponse mockResponse = WholeCalendarResponse.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .goalStartDate(goal.getStartDate())
                .goalEndDate(goal.getEndDate())
                .totalCost(totalCost)
                .dailyList(dailyInfoList)
                .categoryCalendarInfo(categoryCalendarInfo)
                .build();

        when(homeService.getWholeCalendarInfos(memberId)).thenReturn(mockResponse);

        mockMvc.perform(get("/home")
                        .param("date", "2024-01-01")
                        .param("year", "2024")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.calendarDate", is(calendarDate.toString())))
                .andExpect(jsonPath("$.result.goalId", is(goal.getId().intValue())))
                .andExpect(jsonPath("$.result.goalTitle", is(goal.getTitle())))
                .andExpect(jsonPath("$.result.goalBudget", is(goal.getTotalBudget().intValue())))
                .andExpect(jsonPath("$.result.goalStartDate", is(goal.getStartDate().toString())))
                .andExpect(jsonPath("$.result.goalEndDate", is(goal.getEndDate().toString())))
                .andExpect(jsonPath("$.result.totalCost", is((int) totalCost)))
                .andExpect(jsonPath("$.result.dailyList", hasSize(2)))
                .andExpect(jsonPath("$.result.dailyList[0].dailyBudget", is(5000)))
                .andExpect(jsonPath("$.result.dailyList[0].dailyTotalCost", is(4000)))
                .andExpect(jsonPath("$.result.categoryCalendarInfo", hasSize(2)))
                .andExpect(jsonPath("$.result.categoryCalendarInfo[0].categoryBudget", is(1000)))
                .andExpect(jsonPath("$.result.categoryCalendarInfo[0].categoryTotalCost", is(4000)));
    }
}
