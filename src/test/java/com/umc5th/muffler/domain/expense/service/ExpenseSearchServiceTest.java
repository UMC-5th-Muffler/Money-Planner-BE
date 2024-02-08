package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.SearchResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpenseSearchServiceTest {
    @Autowired
    private ExpenseSearchService expenseSearchService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private MemberRepository memberRepository;

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
        when(expenseRepository.findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "DESC")).thenReturn(expenseSlice);

        SearchResponse response = expenseSearchService.searchExpense(memberId, searchKeyword, 10, "DESC", null, null);

        assertNotNull(response);
        assertEquals(expenses.get(0).getDate(), response.getDailyExpenseList().get(0).getDate());
        assertEquals(expenses.get(1).getDate(), response.getDailyExpenseList().get(1).getDate());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "DESC");
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
        when(expenseRepository.findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "ASC")).thenReturn(expenseSlice);

        SearchResponse response = expenseSearchService.searchExpense(memberId, searchKeyword, 10, "ASC", null, null);

        assertNotNull(response);
        assertEquals(expenses.get(0).getDate(), response.getDailyExpenseList().get(0).getDate());
        assertEquals(expenses.get(1).getDate(), response.getDailyExpenseList().get(1).getDate());
        assertEquals(expenseSlice.hasNext(), response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "ASC");
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
        when(expenseRepository.findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "ASC")).thenReturn(new SliceImpl<>(Collections.emptyList()));

        SearchResponse response = expenseSearchService.searchExpense(memberId, searchKeyword, 10, "ASC", null, null);

        assertNotNull(response);
        assertTrue(response.getDailyExpenseList().isEmpty());
        assertEquals(false, response.isHasNext());

        verify(expenseRepository).findByMemberAndTitleContaining(memberId, searchKeyword, null, null, 10, "ASC");
    }

    @Test
    public void 소비_검색_사용자_없음() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> expenseSearchService.searchExpense(memberId, "", 10, "DESC", null, null));
    }

}