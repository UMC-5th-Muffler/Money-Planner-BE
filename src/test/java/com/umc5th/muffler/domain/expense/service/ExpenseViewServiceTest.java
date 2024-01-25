package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseDetailDto;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.MemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void 일일_소비내역_조회_성공() {

        int pageSize = 10;
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        String memberId = mockMember.getId();

        List<Expense> expenses = ExpenseFixture.createList(10, testDate);
        List<Expense> sortedExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getCreatedAt).reversed())
                .collect(Collectors.toList());
        Slice<Expense> expenseSlice = new SliceImpl<>(sortedExpenses, pageable, false);

        Long dailyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(testDate, memberId)).thenReturn(Optional.of(mockGoal));
        when(expenseRepository.findAllByMemberAndDate(mockMember, testDate, pageable)).thenReturn(expenseSlice);

        DailyExpenseResponse response = expenseViewService.getDailyExpenseDetails(memberId, testDate, pageable);

        assertNotNull(response);
        assertEquals(testDate, response.getDate());
        assertEquals(pageSize, response.getExpenseDetailDtoList().size());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        // expenseId 내림차순 정렬 확인(createdAt 내림차순 정렬 확인)
        List<Long> expenseIds = response.getExpenseDetailDtoList().stream()
                .map(ExpenseDetailDto::getExpenseId)
                .collect(Collectors.toList());
        assertTrue(isSortedDescending(expenseIds));

        verify(goalRepository).findByDateBetween(testDate, memberId);
        verify(expenseRepository).findAllByMemberAndDate(mockMember, testDate, pageable);
    }

    private boolean isSortedDescending(List<Long> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) < list.get(i + 1)) {
                return false;
            }
        }
        return true;
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
        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Member mockMember = MemberFixture.create();

        List<Expense> expenses = ExpenseFixture.createList(20, startDate);
        Slice<Expense> expenseSlice = new SliceImpl<>(expenses, pageable, true);
        Long weeklyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.calculateTotalCostByMemberAndDateBetween(mockMember, startDate, endDate)).thenReturn(weeklyTotalCost);
        when(expenseRepository.findAllByMemberAndDateBetween(mockMember, startDate, endDate, pageable)).thenReturn(expenseSlice);

        WeeklyExpenseResponse response = expenseViewService.getWeeklyExpenseDetails(memberId, date, pageable);

        assertNotNull(response);
        assertEquals(startDate, response.getStartDate());
        assertEquals(endDate, response.getEndDate());
        assertEquals(weeklyTotalCost, response.getWeeklyTotalCost());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).calculateTotalCostByMemberAndDateBetween(mockMember, startDate, endDate);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, startDate, endDate, pageable);
    }
}