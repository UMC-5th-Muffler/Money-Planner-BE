package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.fixture.ExpenseFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseViewService expenseViewService;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @WithMockUser
    void 일일_소비내역_조회() throws Exception {
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        List<Expense> expenses = ExpenseFixture.createList(10, testDate);
        List<ExpenseDetailDto> expenseDetailDtos = expenses.stream()
                .map(expense -> new ExpenseDetailDto(expense.getId(), expense.getTitle(), expense.getCost(), expense.getMemo(), expense.getCategory().getId(), expense.getCategory().getIcon()))
                .collect(Collectors.toList());
        long expDailyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        DailyExpenseResponse mockResponse = DailyExpenseResponse.builder()
                .date(testDate)
                .dailyTotalCost(expDailyTotalCost)
                .expenseDetailList(expenseDetailDtos)
                .build();

        when(expenseViewService.getDailyExpenseDetails(any(), eq(testDate), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/expense/daily")
                        .param("date", testDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.expenseDetailList", hasSize(10)))
                .andExpect(jsonPath("$.result.dailyTotalCost", is((int) expDailyTotalCost)))
                .andExpect(jsonPath("$.result.date", is(testDate.toString())));
    }


    @Test
    @WithMockUser
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
                            .map(expense -> new ExpenseDetailDto(expense.getId(), expense.getTitle(), expense.getCost(), expense.getMemo(), expense.getCategory().getId(), expense.getCategory().getIcon()))
                            .collect(Collectors.toList());

                    Long dailyTotalCost = dailyExpenses.stream().mapToLong(Expense::getCost).sum();
                    return DailyExpensesDto.builder()
                            .date(dailyDate)
                            .dailyTotalCost(dailyTotalCost)
                            .expenseDetailList(expenseDetailDtos)
                            .build();
                })
                .collect(Collectors.toList());

        List<CategoryDetailDto> categoryList = List.of(CategoryDetailDto.builder().id(1L).name("icon").build());

        WeeklyExpenseResponse mockResponse = WeeklyExpenseResponse.builder()
                .categoryList(categoryList)
                .dailyExpenseList(dailyExpensesDtos)
                .build();

        when(expenseViewService.getWeeklyExpenseDetails(any(), any(), eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/expense/weekly")
                        .param("goalId", "1")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dailyExpenseList", hasSize(2))) // 이틀에 대한 데이터가 있는지 확인
                .andExpect(jsonPath("$.result.dailyExpenseList[0].expenseDetailList", hasSize(10))) // 첫 번째 날에 대한 지출이 10개 있는지 확인
                .andExpect(jsonPath("$.result.categoryList", notNullValue()));
    }

    @Test
    @WithMockUser
    public void 홈_소비내역_조회() throws Exception {
        YearMonth yearMonth = YearMonth.of(2024, 1);
        Long goalId = 1L;
        String order = "DESC";
        int page = 0;
        int size = 20;

        List<Expense> expenses = ExpenseFixture.createList(10, LocalDate.of(2024,1,1));
        List<Expense> expenses_e = ExpenseFixture.createList(10, LocalDate.of(2024,1,2));
        expenses.addAll(expenses_e); // 이틀 간의 지출 내역 데이터

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenses.stream().collect(Collectors.groupingBy(Expense::getDate));

        List<DailyExpensesDto> dailyExpensesDtos = expensesByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate dailyDate = entry.getKey();
                    List<Expense> dailyExpenses = entry.getValue();
                    List<ExpenseDetailDto> expenseDetailDtos = dailyExpenses.stream()
                            .map(expense -> ExpenseDetailDto.builder()
                                    .expenseId(expense.getId())
                                    .cost(expense.getCost())
                                    .categoryIcon(expense.getCategory().getIcon())
                                    .title(expense.getTitle())
                                    .build())
                            .collect(Collectors.toList());

                    Long dailyTotalCost = dailyExpenses.stream().mapToLong(Expense::getCost).sum();
                    return DailyExpensesDto.builder()
                            .date(dailyDate)
                            .dailyTotalCost(dailyTotalCost)
                            .expenseDetailList(expenseDetailDtos)
                            .build();
                })
                .collect(Collectors.toList());

        MonthlyExpenseResponse mockResponse = MonthlyExpenseResponse.builder()
                .dailyExpenseList(dailyExpensesDtos)
                .build();

        when(expenseViewService.getMonthlyExpenses(any(), eq(yearMonth), eq(goalId), eq(order), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/expense/monthly")
                        .param("yearMonth", String.valueOf(yearMonth))
                        .param("order", order)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("goalId", String.valueOf(goalId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", notNullValue()))
                .andExpect(jsonPath("$.result.dailyExpenseList", hasSize(2))) // 이틀에 대한 데이터가 있는지 확인
                .andExpect(jsonPath("$.result.dailyExpenseList[0].expenseDetailList", hasSize(10))); // 첫 번째 날에 대한 지출이 10개 있는지 확인
    }

    @Test
    @WithMockUser
    public void 소비_하나_조회() throws Exception {
        Expense mockExpense = ExpenseFixture.create(any());
        Long expenseId = mockExpense.getId();
        ExpenseDto mockExpenseDto = ExpenseConverter.toExpenseDto(mockExpense);

        when(expenseViewService.getExpense(expenseId)).thenReturn(mockExpenseDto);

        mockMvc.perform(get("/expense/{id}", expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.expenseId").value(expenseId))
                .andExpect(jsonPath("$.result.title").value(mockExpense.getTitle()))
                .andExpect(jsonPath("$.result.categoryName").value(mockExpense.getCategory().getName()));
    }

    @Test
    @WithMockUser
    public void 소비_검색_성공() throws Exception {

        ExpenseDetailDto dto = ExpenseDetailDto.builder()
                .expenseId(1L)
                .title("title")
                .cost(1000L)
                .categoryIcon("icon")
                .build();

        DailyExpensesDto expenseDetailsDto = DailyExpensesDto.builder()
                .date(LocalDate.now())
                .expenseDetailList(List.of(dto))
                .build();

        SearchResponse mockResponse = SearchResponse.builder()
                .dailyExpenseList(List.of(expenseDetailsDto))
                .hasNext(false)
                .build();

        when(expenseService.searchExpense("user", "title", 0, 2, "ASC")).thenReturn(mockResponse);

        mockMvc.perform(get("/expense/search")
                        .param("title", "title")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dailyExpenseList", hasSize(1)))
                .andExpect(jsonPath("$.result.hasNext", is(mockResponse.isHasNext())));
    }
}