package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.expense.service.HomeService;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.expense.dto.homeDto.CategoryCalendarInfo;
import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.fixture.*;
import com.umc5th.muffler.global.response.exception.HomeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public void 목표O_전체조회_성공() throws HomeException {
        // given
        Long memberId = 1L;

        LocalDate testDate = LocalDate.of(2024, 1, 1);
        LocalDate testDate2 = LocalDate.of(2024, 1, 2);
        Member mockMember = MemberEntityFixture.create();
        Category mockCategory1 = CategoryFixture.CATEGORY_ZERO;
        Category mockCategory2 = CategoryFixture.CATEGORY_ONE;
        Goal mockGoal = GoalFixture.createDetail();

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, LocalDate.of(2024, 1, 1), mockCategory1.getId());
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, LocalDate.of(2024, 1, 2), mockCategory2.getId());
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        List<Category> categoryList = Arrays.asList(mockCategory1, mockCategory2);
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.of(mockGoal));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, testDate, testDate2)).thenReturn(expenseList);
        when(expenseRepository.findDistinctCategoriesBetweenDates(mockMember, testDate, testDate2)).thenReturn(categoryList);
        CategoryGoal categoryGoal = mockGoal.getCategoryGoals().stream().findFirst().get();
        Long id = categoryGoal.getCategory().getId();
        when(expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(mockMember, id, mockGoal.getStartDate(), mockGoal.getEndDate())).thenReturn(expenseList1);

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(testDate, 2024, 1);

        // then
        assertNotNull(response);
        assertEquals(testDate, response.getCalendarDate());
        assertEquals(mockGoal.getId(), response.getGoalId());
        assertEquals(mockGoal.getTitle(), response.getGoalTitle());
        assertEquals(mockGoal.getTotalBudget(), response.getGoalBudget());
        assertEquals(mockGoal.getStartDate(), response.getGoalStartDate());
        assertEquals(mockGoal.getEndDate(), response.getGoalEndDate());
        assertEquals(goalTotalCost, response.getTotalCost());

        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, testDate, testDate2);
        verify(expenseRepository).findDistinctCategoriesBetweenDates(mockMember, testDate, testDate2);
        verify(expenseRepository).findAllByMemberAndCategoryIdAndDateBetween(mockMember, id, mockGoal.getStartDate(), mockGoal.getEndDate());
    }

    @Test
    public void 목표X_전체조회_성공() throws HomeException {
        // given
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;

        Member mockMember = MemberEntityFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.empty());

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(testDate, 2024, 1);

        // then
        assertNotNull(response);
        assertNull(response.getGoalId());
    }

    @Test
    public void 사용자가_없는_경우() throws HomeException {
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(HomeException.class, () -> homeService.getWholeCalendarInfos(testDate, 2024, 1));
    }

    @Test
    public void 목표_포함X_달_조회() throws HomeException {
        // given
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        Member mockMember = MemberEntityFixture.create();
        Goal mockGoal = GoalFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.of(mockGoal));

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(testDate, 2024, 2);

        // then
        assertNotNull(response);
        assertNull(response.getGoalId());
    }

    @Test
    public void INACTIVE_카테고리_필터링_X() throws HomeException {
        // given
        Long memberId = 1L;

        LocalDate testDate = LocalDate.of(2024, 1, 1);
        LocalDate testDate2 = LocalDate.of(2024, 1, 2);
        Member mockMember = MemberEntityFixture.create();
        Category mockCategory1 = CategoryFixture.CATEGORY_ZERO; // ACTIVE
        Category mockCategory2 = CategoryFixture.CATEGORY_THREE; // INACTIVE
        Goal mockGoal = GoalFixture.createDetail();

        List<Expense> expenseList1 = ExpenseFixture.createCategoryExpenseList(10, LocalDate.of(2024, 1, 1), mockCategory1.getId());
        List<Expense> expenseList2 = ExpenseFixture.createCategoryExpenseList(10, LocalDate.of(2024, 1, 2), mockCategory2.getId());
        List<Expense> expenseList = Stream.concat(expenseList1.stream(), expenseList2.stream()).collect(Collectors.toList());
        List<Category> categoryList = Arrays.asList(mockCategory1, mockCategory2);
        Long goalTotalCost = expenseList.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.of(mockGoal));
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, testDate, testDate2)).thenReturn(expenseList);
        when(expenseRepository.findDistinctCategoriesBetweenDates(mockMember, testDate, testDate2)).thenReturn(categoryList);
        CategoryGoal categoryGoal = mockGoal.getCategoryGoals().stream().findFirst().get();
        Long id = categoryGoal.getCategory().getId();
        when(expenseRepository.findAllByMemberAndCategoryIdAndDateBetween(mockMember, id, mockGoal.getStartDate(), mockGoal.getEndDate())).thenReturn(expenseList1);

        // when
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(testDate, 2024, 1);

        // then
        assertNotNull(response);
        assertEquals(mockGoal.getId(), response.getGoalId());
        assertEquals(goalTotalCost, response.getTotalCost());

        List<CategoryCalendarInfo> categoryInfos = response.getCategoryCalendarInfo();
        assertTrue(categoryInfos.stream().anyMatch(info -> info.getId().equals(mockCategory1.getId())));
        assertTrue(categoryInfos.stream().noneMatch(info -> info.getId().equals(mockCategory2.getId())));

        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, testDate, testDate2);
        verify(expenseRepository).findDistinctCategoriesBetweenDates(mockMember, testDate, testDate2);
        verify(expenseRepository).findAllByMemberAndCategoryIdAndDateBetween(mockMember, id, mockGoal.getStartDate(), mockGoal.getEndDate());
    }
}
