package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsDto;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.dto.SearchResponse;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.exception.MemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpenseServiceTest {
    @Autowired
    private ExpenseService expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    public void 일일_소비내역_조회_성공() {

        int pageSize = 10;
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Pageable pageable = PageRequest.of(0, pageSize);
        String memberId = "1";

        Member mockMember = MemberFixture.create();

        List<Expense> expenses = ExpenseFixture.createList(10, testDate);
        Slice<Expense> expenseSlice = new SliceImpl<>(expenses, pageable, false);
        Long dailyTotalCost = expenses.stream().mapToLong(Expense::getCost).sum();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findAllByMemberAndDate(mockMember, testDate, pageable)).thenReturn(expenseSlice);
        when(expenseRepository.calculateTotalCostByMemberAndDate(mockMember, testDate)).thenReturn(dailyTotalCost);

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(testDate, pageable);

        assertNotNull(response);
        assertEquals(testDate, response.getDate());
        assertEquals(pageSize, response.getExpenseDetailDtoList().size());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).calculateTotalCostByMemberAndDate(mockMember, testDate);
        verify(expenseRepository).findAllByMemberAndDate(mockMember, testDate, pageable);
    }

    @Test
    public void 일일_소비내역_조회_멤버가없을경우() {

        LocalDate testDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> {
            expenseService.getDailyExpenseDetails(testDate, pageable);});
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

        WeeklyExpenseDetailsResponse response = expenseService.getWeeklyExpenseDetails(date, pageable);

        assertNotNull(response);
        assertEquals(startDate, response.getStartDate());
        assertEquals(endDate, response.getEndDate());
        assertEquals(weeklyTotalCost, response.getWeeklyTotalCost());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).calculateTotalCostByMemberAndDateBetween(mockMember, startDate, endDate);
        verify(expenseRepository).findAllByMemberAndDateBetween(mockMember, startDate, endDate, pageable);
    }

    @Test
    public void 소비_검색_DESC_성공() {
        int pageSize = 2;
        String memberId = "1";
        String searchKeyword = "O";
        Sort sort = Sort.by(Sort.Direction.DESC,"date").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Member mockMember = MemberFixture.create();
        List<Expense> expenses = List.of(ExpenseFixture.EXPENSE_THREE, ExpenseFixture.EXPENSE_TWO, ExpenseFixture.EXPENSE_ONE);
        Slice<Expense> expenseSlice = new SliceImpl<>(expenses, pageable, true);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findByMemberAndTitleContaining(mockMember, searchKeyword, pageable)).thenReturn(expenseSlice);

        SearchResponse response = expenseService.searchExpense(memberId, searchKeyword, 0, 2, "DESC");

        assertNotNull(response);
        assertEquals(expenses.get(0).getDate(), response.getDailyExpenseList().get(0).getDate());
        assertEquals(expenses.get(1).getDate(), response.getDailyExpenseList().get(1).getDate());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(mockMember, searchKeyword, pageable);
    }

    @Test
    public void 소비_검색_ASC_성공() {
        int pageSize = 2;
        String memberId = "1";
        String searchKeyword = "title";
        Sort sort = Sort.by(Sort.Direction.ASC,"date").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Member mockMember = MemberFixture.create();
        List<Expense> expenses = List.of(ExpenseFixture.EXPENSE_ONE, ExpenseFixture.EXPENSE_TWO, ExpenseFixture.EXPENSE_THREE);
        Slice<Expense> expenseSlice = new SliceImpl<>(expenses, pageable, true);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findByMemberAndTitleContaining(mockMember, searchKeyword, pageable)).thenReturn(expenseSlice);

        SearchResponse response = expenseService.searchExpense(memberId, searchKeyword, 0, 2, "ASC");

        assertNotNull(response);
        assertEquals(expenses.get(0).getDate(), response.getDailyExpenseList().get(0).getDate());
        assertEquals(expenses.get(1).getDate(), response.getDailyExpenseList().get(1).getDate());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(mockMember, searchKeyword, pageable);
    }

    @Test
    public void 소비_검색_결과_없음() {
        int pageSize = 2;
        String memberId = "1";
        String searchKeyword = "O";
        Sort sort = Sort.by(Sort.Direction.ASC,"date").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findByMemberAndTitleContaining(mockMember, searchKeyword, pageable)).thenReturn(new SliceImpl<>(Collections.emptyList()));

        SearchResponse response = expenseService.searchExpense(memberId, searchKeyword, 0, 2, "ASC");

        assertNotNull(response);
        assertTrue(response.getDailyExpenseList().isEmpty());
        assertEquals(false, response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(mockMember, searchKeyword, pageable);
    }

    @Test
    public void 소비_검색_사용자_없음() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> expenseService.searchExpense(memberId, "", 0, 1, "DESC"));
    }
}