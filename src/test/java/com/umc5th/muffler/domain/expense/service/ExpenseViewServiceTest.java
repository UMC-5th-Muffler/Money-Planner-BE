package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.DailyPlanFixture;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.MemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExpenseViewServiceTest {
    @Autowired
    private ExpenseViewService expenseViewService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private GoalRepository goalRepository;

    @MockBean
    private DailyPlanRepository dailyPlanRepository;

    @Test
    public void 일일_소비내역_조회_성공() {

        int pageSize = 10;
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Member mockMember = MemberFixture.create();
        DailyPlan dailyPlan = DailyPlanFixture.DAILY_PLAN_ONE;
        String memberId = mockMember.getId();

        List<Expense> expenses = ExpenseFixture.createList(10, testDate);
        List<Expense> sortedExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getCreatedAt).reversed())
                .collect(Collectors.toList());
        Slice<Expense> expenseSlice = new SliceImpl<>(sortedExpenses, pageable, false);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findAllByMemberAndDate(mockMember, testDate, pageable)).thenReturn(expenseSlice);

        DailyExpenseResponse response = expenseViewService.getDailyExpenseDetails(memberId, testDate, pageable);

        assertNotNull(response);
        assertEquals(pageSize, response.getExpenseDetailList().size());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        // expenseId 내림차순 정렬 확인(createdAt 내림차순 정렬 확인)
        List<Long> expenseIds = response.getExpenseDetailList().stream()
                .map(ExpenseDetailDto::getExpenseId)
                .collect(Collectors.toList());
        assertTrue(isSortedDescending(expenseIds));

        verify(expenseRepository).findAllByMemberAndDate(mockMember, testDate, pageable);
    }

    @Test
    public void 일일_소비내역_조회_멤버가없을경우() {

        LocalDate testDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> {
            expenseViewService.getDailyExpenseDetails(memberId, testDate, pageable);});
    }

    @Test
    public void 주간_소비내역_조회_성공(){
        int pageSize = 10;
        String memberId = "1";
        Pageable pageable = PageRequest.of(0, pageSize);
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDate weeklyStartDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weeklyEndDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();

        LocalDate expenseStartDate = mockGoal.getStartDate().isBefore(weeklyStartDate) ? weeklyStartDate : mockGoal.getStartDate();
        LocalDate expenseEndDate = mockGoal.getEndDate().isAfter(weeklyEndDate) ? weeklyEndDate : mockGoal.getEndDate();

        List<Expense> expenses = ExpenseFixture.createList(20, weeklyStartDate);
        Page<Expense> expenseSlice = new PageImpl<>(expenses, pageable, expenses.size());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(mockGoal.getId())).thenReturn(Optional.of(mockGoal));
        when(expenseRepository.findAllByMemberAndDateAndCategoryId(memberId, expenseStartDate, expenseEndDate, null, pageable)).thenReturn(expenseSlice);

        WeeklyExpenseResponse response = expenseViewService.getWeeklyExpenseDetails(memberId, mockGoal.getId(), weeklyStartDate, weeklyEndDate, pageable);

        assertNotNull(response);
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(memberRepository).findById(memberId);
        verify(goalRepository).findById(mockGoal.getId());
        verify(expenseRepository).findAllByMemberAndDateAndCategoryId(memberId, expenseStartDate, expenseEndDate, null, pageable);
    }

    @Test
    public void 홈_소비_조회_성공() {
        String memberId = "1";
        YearMonth yearMonth = YearMonth.of(2024, 1);
        Long goalId = 1L;
        String order = "ASC";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(order.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "date"));

        Member mockMember = MemberFixture.create();
        Goal mockGoal = mock(Goal.class);
        List<DailyPlan> mockDailyPlans = Arrays.asList(DailyPlanFixture.DAILY_PLAN_ONE, DailyPlanFixture.DAILY_PLAN_TWO);
        List<Expense> expenses1 = ExpenseFixture.createListWithStartNum(10, 10, LocalDate.of(2024, 1, 1));
        List<Expense> expenses2 = ExpenseFixture.createListWithStartNum(1, 10, LocalDate.of(2024, 1, 2));
        List<Expense> expenses = new ArrayList<>(expenses2);
        expenses.addAll(expenses1);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Expense> sortedExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate))
                .sorted(Comparator.comparing(Expense::getCreatedAt).reversed())
                .collect(Collectors.toList());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));

        when(mockGoal.getStartDate()).thenReturn(startDate);
        when(mockGoal.getEndDate()).thenReturn(endDate);
        when(mockGoal.getDailyPlans()).thenReturn(mockDailyPlans);

        Page<Expense> expenseSlice = new PageImpl<>(sortedExpenses, pageable, expenses.size());

        when(expenseRepository.findAllByMemberAndDateAndCategoryId(memberId, startDate, endDate, null, pageable)).thenReturn(expenseSlice);

        MonthlyExpenseResponse response = expenseViewService.getMonthlyExpenses(memberId, yearMonth, goalId, order, pageable);

        // expenseId 내림차순 정렬 확인(date 오름차순, createdAt 내림차순 정렬 확인)
        List<Long> expenseIds = response.getDailyExpenseList().stream()
                .sorted(Comparator.comparing(DailyExpensesDto::getDate))
                .flatMap(dto -> dto.getExpenseDetailList().stream()
                        .sorted(Comparator.comparing(ExpenseDetailDto::getExpenseId).reversed()))
                .map(ExpenseDetailDto::getExpenseId)
                .collect(Collectors.toList());

        assertTrue(isSortedDescending(expenseIds));

        assertNotNull(response);
        assertEquals(expenseSlice.hasNext(), response.isHasNext());
        assertEquals(2, response.getDailyExpenseList().size());
        assertTrue(response.getDailyExpenseList().stream().allMatch(dto -> dto.getDate().compareTo(startDate) >= 0 && dto.getDate().compareTo(endDate) <= 0));

        verify(memberRepository).findById(memberId);
        verify(goalRepository).findById(goalId);
        verify(expenseRepository).findAllByMemberAndDateAndCategoryId(memberId, startDate, endDate, null, pageable);
    }

    @Test
    public void 홈_소비_조회_카테고리별_성공() {
        String memberId = "1";
        YearMonth yearMonth = YearMonth.of(2024, 1);
        Long goalId = 1L;
        Long categoryId = 2L; // 테스트용 카테고리 ID
        String order = "ASC";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(order.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "date"));

        Member mockMember = MemberFixture.create();
        Goal mockGoal = mock(Goal.class);
        List<DailyPlan> mockDailyPlans = Arrays.asList(DailyPlanFixture.DAILY_PLAN_ONE, DailyPlanFixture.DAILY_PLAN_TWO);
        List<Expense> expenses1 = ExpenseFixture.createListWithStartNum(10, 10, LocalDate.of(2024, 1, 1));
        List<Expense> expenses2 = ExpenseFixture.createListWithStartNum(1, 10, LocalDate.of(2024, 1, 2));
        List<Expense> expenses = new ArrayList<>(expenses2);
        expenses.addAll(expenses1);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<Expense> filteredExpenses = expenses.stream()
                .filter(expense -> expense.getCategory().getId().equals(categoryId))
                .sorted(Comparator.comparing(Expense::getDate))
                .sorted(Comparator.comparing(Expense::getCreatedAt).reversed())
                .collect(Collectors.toList());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));
        when(mockGoal.getStartDate()).thenReturn(startDate);
        when(mockGoal.getEndDate()).thenReturn(endDate);
        when(mockGoal.getDailyPlans()).thenReturn(mockDailyPlans);

        Page<Expense> expenseSlice = new PageImpl<>(filteredExpenses, pageable, filteredExpenses.size());
        when(expenseRepository.findAllByMemberAndDateAndCategoryId(memberId, startDate, endDate, categoryId, pageable)).thenReturn(expenseSlice);

        MonthlyExpenseResponse response = expenseViewService.getMonthlyExpensesWithCategory(memberId, yearMonth, goalId, categoryId, order, pageable);

        // expenseId 내림차순 정렬 확인(date 오름차순, createdAt 내림차순 정렬 확인)
        List<Long> expenseIds = response.getDailyExpenseList().stream()
                .sorted(Comparator.comparing(DailyExpensesDto::getDate))
                .flatMap(dto -> dto.getExpenseDetailList().stream()
                        .sorted(Comparator.comparing(ExpenseDetailDto::getExpenseId).reversed()))
                .map(ExpenseDetailDto::getExpenseId)
                .collect(Collectors.toList());

        assertTrue(isSortedDescending(expenseIds));
        assertNotNull(response);
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        // 특정 카테고리 소비 내역이 포함되었는지 확인
        assertTrue(response.getDailyExpenseList().stream()
                .flatMap(dto -> dto.getExpenseDetailList().stream())
                .allMatch(dto -> dto.getCategoryIcon().equals(String.valueOf(categoryId))));

        verify(expenseRepository).findAllByMemberAndDateAndCategoryId(memberId, startDate, endDate, categoryId, pageable);
    }

    @Test
    public void 소비_하나_조회_성공(){
        String memberId = "1";
        Expense mockExpense = ExpenseFixture.create(any());
        Member mockMember = MemberFixture.create();
        Long expenseId = mockExpense.getId();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findByIdJoin(expenseId)).thenReturn(Optional.of(mockExpense));
        ExpenseDto result = expenseViewService.getExpense(any(), expenseId);

        assertNotNull(result);
        assertEquals(expenseId, result.getExpenseId());
        assertEquals(mockExpense.getDate(), result.getDate());
        assertEquals(mockExpense.getTitle(), result.getTitle());
        assertEquals(mockExpense.getCost(), result.getCost());
        assertEquals(mockExpense.getCategory().getName(), result.getCategoryName());

        verify(expenseRepository).findByIdJoin(expenseId);
    }

    @Test
    public void 소비_하나_조회_expenseId가_유효하지_않는_경우(){
        String memberId = "1";
        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findByIdJoin(any())).thenReturn(Optional.empty());

        assertThrows(ExpenseException.class, () -> {
            expenseViewService.getExpense(memberId, any());
        }, ErrorCode.EXPENSE_NOT_FOUND.getMessage());

        verify(expenseRepository).findByIdJoin(any());
    }


    private boolean isSortedDescending(List<Long> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) < list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
}