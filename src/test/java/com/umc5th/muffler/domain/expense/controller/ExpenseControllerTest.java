package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.fixture.ExpenseFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseViewService expenseViewService;


    @Test
    void 일일_소비내역_조회() throws Exception{

        LocalDate testDate = LocalDate.of(2024, 1, 1);
        List<Expense> expenses = ExpenseFixture.createList(10, testDate);
        List<ExpenseDetailDto> expenseDetailDtos = expenses.stream()
                .map(expense -> new ExpenseDetailDto(expense.getId(), expense.getTitle(), expense.getCost(), expense.getCategory().getId(), expense.getCategory().getIcon()))
                .collect(Collectors.toList());
        List<CategoryDetailDto> categoryList = List.of(CategoryDetailDto.builder().id(1L).name("icon").build());
        long expDailyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        DailyExpenseResponse mockResponse = DailyExpenseResponse.builder()
                .date(testDate)
                .dailyTotalCost(expDailyTotalCost)
                .expenseDetailDtoList(expenseDetailDtos)
                .categoryList(categoryList)
                .build();

        when(expenseViewService.getDailyExpenseDetails(eq(testDate), any(Pageable.class))).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/expense/daily")
                        .param("date", testDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.expenseDetailDtoList", hasSize(10)))
                .andExpect(jsonPath("$.result.dailyTotalCost", is((int) expDailyTotalCost)))
                .andExpect(jsonPath("$.result.date", is(testDate.toString())));
    }

    @Test
    public void 주간_소비내역_조회() throws Exception{

        LocalDate todayDate = LocalDate.of(2024, 1, 1);
        LocalDate startDate = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = todayDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<Expense> expenses = ExpenseFixture.createList(10, startDate);
        List<Expense> expenses_e = ExpenseFixture.createList(10, endDate);
        expenses.addAll(expenses_e); // 이틀 간의 지출 내역 데이터

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenses.stream().collect(Collectors.groupingBy(Expense::getDate));

        List<DailyExpensesDto> dailyExpensesDtos = expensesByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate dailyDate = entry.getKey();
                    List<Expense> dailyExpenses = entry.getValue();
                    List<ExpenseDetailDto> expenseDetailDtos = dailyExpenses.stream()
                            .map(expense -> new ExpenseDetailDto(expense.getId(), expense.getTitle(), expense.getCost(), expense.getCategory().getId(), expense.getCategory().getIcon()))
                            .collect(Collectors.toList());

                    Long dailyTotalCost = dailyExpenses.stream().mapToLong(Expense::getCost).sum();
                    return DailyExpensesDto.builder()
                            .date(dailyDate)
                            .dailyTotalCost(dailyTotalCost)
                            .expenseDetailDtoList(expenseDetailDtos)
                            .build();
                })
                .collect(Collectors.toList());

        List<CategoryDetailDto> categoryList = List.of(CategoryDetailDto.builder().id(1L).name("icon").build());
        long expWeeklyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        WeeklyExpenseResponse mockResponse = WeeklyExpenseResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .weeklyTotalCost(expenses.stream().mapToLong(Expense::getCost).sum())
                .categoryList(categoryList)
                .dailyExpenseList(dailyExpensesDtos)
                .build();

        when(expenseViewService.getWeeklyExpenseDetails(eq(todayDate), any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/expense/weekly")
                        .param("date", todayDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.startDate", is(startDate.toString())))
                .andExpect(jsonPath("$.result.endDate", is(endDate.toString())))
                .andExpect(jsonPath("$.result.weeklyTotalCost", is((int) expWeeklyTotalCost)))
                .andExpect(jsonPath("$.result.dailyExpenseList", hasSize(2))) // 이틀에 대한 데이터가 있는지 확인
                .andExpect(jsonPath("$.result.dailyExpenseList[0].expenseDetailDtoList", hasSize(10))) // 첫 번째 날에 대한 지출이 10개 있는지 확인
                .andExpect(jsonPath("$.result.categoryList", notNullValue()));
    }

}