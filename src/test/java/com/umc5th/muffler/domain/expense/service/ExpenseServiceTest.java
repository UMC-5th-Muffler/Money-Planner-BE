package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryEntityFixture;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.MemberEntityFixture;
import com.umc5th.muffler.global.response.exception.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ExpenseServiceTest {
    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void 일일_소비내역_조회_성공() {

        LocalDate testDate = LocalDate.of(2024, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);
        Long memberId = 1L;

        Member mockMember = MemberEntityFixture.create();
        List<Expense> expenses = ExpenseFixture.createList(10);
        Slice<Expense> expenseSlice = new SliceImpl<>(expenses, pageable, false);

        List<Category> memberCategories = CategoryEntityFixture.createList(5);
        List<Category> commonCategories = CategoryEntityFixture.createList(5);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(expenseRepository.findAllByMemberAndDate(mockMember, testDate, pageable)).thenReturn(expenseSlice);
        when(categoryRepository.findAllByMember(mockMember)).thenReturn(memberCategories);
        when(categoryRepository.findAllWithNoMember()).thenReturn(commonCategories);

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(testDate, pageable);

        assertEquals(testDate, response.getDate());
        assertFalse(response.isHasNext());
        assertEquals(10, response.getExpenseDetailDtoList().size());
    }

    @Test
    public void 일일_소비내역_조회_멤버가없을경우() {

        LocalDate testDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> {
            expenseService.getDailyExpenseDetails(testDate, pageable);});
    }

}