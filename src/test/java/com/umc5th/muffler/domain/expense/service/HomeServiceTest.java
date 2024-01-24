package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.homeDto.OtherGoalsInfo;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.expense.dto.homeDto.CategoryCalendarInfo;
import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.fixture.*;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HomeServiceTest {

    @Autowired
    private HomeService homeService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private GoalRepository goalRepository;
    @MockBean
    private ExpenseRepository expenseRepository;


    @Test
    public void 오늘_날짜_목표O_전체조회_성공() {
        // given
        String memberId = "1";

        LocalDate testStartDate = LocalDate.now();
        LocalDate test1 = LocalDate.of(2024, 1, 1); LocalDate test2 = LocalDate.of(2024, 1, 2);
        YearMonth yearMonth = YearMonth.of(testStartDate.getYear(), testStartDate.getMonthValue());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        Member mockMember = MemberFixture.create();
        Category mockCategory1 = CategoryFixture.CATEGORY_ONE;
        Category mockCategory2 = CategoryFixture.CATEGORY_TWO;
        Goal mockGoal = GoalFixture.create();
        Goal mockGoalToday = GoalFixture.createToday();
        List<Goal> goalList = Arrays.asList(mockGoal, mockGoalToday);

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, mockGoalToday.getStartDate(), mockCategory1);
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, mockGoalToday.getEndDate(), mockCategory2);
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)).thenReturn(Optional.of(goalList));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, mockGoalToday.getStartDate(), mockGoalToday.getEndDate())).thenReturn(expenseList);

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(memberId);

        // then
        assertNotNull(response);
        assertEquals(mockGoalToday.getId(), response.getGoalId());
        assertEquals(mockGoalToday.getTitle(), response.getGoalTitle());
        assertEquals(mockGoalToday.getTotalBudget(), response.getGoalBudget());
        assertEquals(mockGoalToday.getStartDate(), response.getGoalStartDate());
        assertEquals(mockGoalToday.getEndDate(), response.getGoalEndDate());
        assertEquals(goalTotalCost, response.getTotalCost());
        assertEquals(mockCategory1.getId(), response.getCategoryCalendarInfo().stream().map(CategoryCalendarInfo::getId).findFirst().get());
        assertEquals(test1, response.getOtherGoalsInfo().stream().map(OtherGoalsInfo::getOtherStartDate).findFirst().get());
        assertEquals(test2, response.getOtherGoalsInfo().stream().map(OtherGoalsInfo::getOtherEndDate).findFirst().get());

        verify(goalRepository).findGoalsByMonth(startOfMonth, endOfMonth, memberId);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, mockGoalToday.getStartDate(), mockGoalToday.getEndDate());
    }

    @Test
    public void 오늘_날짜_목표X_전체조회_성공() {
        // given
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        String memberId = "1";

        Member mockMember = MemberFixture.MEMBER_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.empty());

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(memberId);

        // then
        assertNotNull(response);
        assertNull(response.getGoalId());
    }

    @Test
    public void 목표_선택_전체조회_성공() {
        // given
        String memberId = "1";
        Long goalId = 1L;

        LocalDate testStartDate = LocalDate.of(2024, 1, 1);
        YearMonth yearMonth = YearMonth.of(testStartDate.getYear(), testStartDate.getMonthValue());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        Member mockMember = MemberFixture.create();
        Category mockCategory1 = CategoryFixture.CATEGORY_ONE;
        Category mockCategory2 = CategoryFixture.CATEGORY_TWO;
        Goal mockGoal = GoalFixture.create();
        Goal mockGoalToday = GoalFixture.createToday();
        List<Goal> goalList = Arrays.asList(mockGoal, mockGoalToday);

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, mockGoal.getStartDate(), mockCategory1);
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, mockGoal.getEndDate(), mockCategory2);
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));
        when(goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)).thenReturn(Optional.of(goalList));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, mockGoal.getStartDate(), mockGoal.getEndDate())).thenReturn(expenseList);

        // when
        WholeCalendarResponse response = homeService.getGoalCalendarInfos(goalId, memberId);

        // then
        assertNotNull(response);
        assertEquals(mockGoal.getId(), response.getGoalId());
        assertEquals(mockGoal.getTitle(), response.getGoalTitle());
        assertEquals(mockGoal.getTotalBudget(), response.getGoalBudget());
        assertEquals(mockGoal.getStartDate(), response.getGoalStartDate());
        assertEquals(mockGoal.getEndDate(), response.getGoalEndDate());
        assertEquals(goalTotalCost, response.getTotalCost());
        assertEquals(mockCategory1.getId(), response.getCategoryCalendarInfo().stream().map(CategoryCalendarInfo::getId).findFirst().get());
        assertEquals(LocalDate.now(), response.getOtherGoalsInfo().stream().map(OtherGoalsInfo::getOtherStartDate).findFirst().get());
        assertEquals(LocalDate.now().plusDays(1), response.getOtherGoalsInfo().stream().map(OtherGoalsInfo::getOtherEndDate).findFirst().get());

        verify(goalRepository).findGoalsByMonth(startOfMonth, endOfMonth, memberId);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, mockGoal.getStartDate(), mockGoal.getEndDate());
    }

    @Test
    public void 목표_선택_존재X_조회_실패() {
        String memberId = "1";
        Long goalId = 1L;
        Member mockMember = MemberFixture.MEMBER_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(GoalException.class, () -> homeService.getGoalCalendarInfos(goalId, memberId));
    }

    @Test
    public void 사용자가_없는_경우() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> homeService.getWholeCalendarInfos(memberId));
    }

    @Test
    public void 달_변경_조회_성공() {
        // given
        String memberId = "1";
        Long goalId = 3L;

        Integer testYear = 2024; Integer testMonth = 2;
        LocalDate testDate = LocalDate.of(2024, 2, 1);
        YearMonth yearMonth = YearMonth.of(testDate.getYear(), testDate.getMonthValue());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        Member mockMember = MemberFixture.MEMBER_ONE;
        Category mockCategory1 = CategoryFixture.CATEGORY_ONE;
        Category mockCategory2 = CategoryFixture.CATEGORY_TWO;
        Goal mockGoal = GoalFixture.createMonth();
        List<Goal> goalList = Arrays.asList(mockGoal);

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, mockGoal.getStartDate(), mockCategory1);
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, mockGoal.getEndDate(), mockCategory2);
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)).thenReturn(Optional.of(goalList));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, mockGoal.getStartDate(), mockGoal.getEndDate())).thenReturn(expenseList);

        // when
        WholeCalendarResponse response = homeService.getTurnPage(goalId, memberId, testYear, testMonth);

        // then
        assertNotNull(response);
        assertEquals(mockGoal.getId(), response.getGoalId());
        assertEquals(mockGoal.getTitle(), response.getGoalTitle());
        assertEquals(mockGoal.getTotalBudget(), response.getGoalBudget());
        assertEquals(testDate, response.getGoalStartDate());
        assertEquals(mockGoal.getEndDate(), response.getGoalEndDate());
        assertEquals(goalTotalCost, response.getTotalCost());
        assertEquals(mockCategory1.getId(), response.getCategoryCalendarInfo().stream().map(CategoryCalendarInfo::getId).findFirst().get());

        verify(goalRepository).findGoalsByMonth(startOfMonth, endOfMonth, memberId);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, mockGoal.getStartDate(), mockGoal.getEndDate());
    }

    @Test
    public void 카테고리_필터링_성공() {
        // given
        String memberId = "1";

        LocalDate testStartDate = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(testStartDate.getYear(), testStartDate.getMonthValue());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        Member mockMember = MemberFixture.create();
        Category mockCategory1 = CategoryFixture.CATEGORY_THREE; // INACTIVE
        Category mockCategory2 = CategoryFixture.CATEGORY_FOUR; // isVisible = false
        Goal mockGoalToday = GoalFixture.createToday();
        List<Goal> goalList = List.of(mockGoalToday);;

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, mockGoalToday.getStartDate(), mockCategory1);
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, mockGoalToday.getEndDate(), mockCategory2);
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findGoalsByMonth(startOfMonth, endOfMonth, memberId)).thenReturn(Optional.of(goalList));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, mockGoalToday.getStartDate(), mockGoalToday.getEndDate())).thenReturn(expenseList);

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(memberId);

        // then
        assertNotNull(response);
        assertEquals(mockGoalToday.getId(), response.getGoalId());
        assertEquals(goalTotalCost, response.getTotalCost());

        List<CategoryCalendarInfo> categoryInfos = response.getCategoryCalendarInfo();
        assertTrue(categoryInfos.stream().noneMatch(info -> info.getId().equals(mockCategory1.getId())));
        assertTrue(categoryInfos.stream().noneMatch(info -> info.getId().equals(mockCategory2.getId())));

        verify(goalRepository).findGoalsByMonth(startOfMonth, endOfMonth, memberId);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, mockGoalToday.getStartDate(), mockGoalToday.getEndDate());
    }
}
