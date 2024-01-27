package com.umc5th.muffler.domain.expense.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseRequest;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class ExpenseUpdateTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private DailyPlanRepository dailyPlanRepository;
    @InjectMocks
    private ExpenseUpdateService expenseService;
    @Captor
    private ArgumentCaptor<Expense> expenseArgumentCaptor;
    @Captor
    private ArgumentCaptor<DailyPlan> dailyPlanArgumentCaptor;

    @Test
    @Transactional
    void 정상_수정_전체_금액_증가() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 150L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.of(expense));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(category));
        given(dailyPlanRepository.findDailyPlanByDateAndMemberId(any(LocalDate.class), any(String.class)))
                .willReturn(Optional.of(dailyPlan));
        given(expenseRepository.save(any(Expense.class))).willReturn(expense);
        given(dailyPlanRepository.save(any(DailyPlan.class))).willReturn(dailyPlan);

        // when
        expenseService.updateExpense(member.getId(), request);

        // then
        verify(expenseRepository).save(expenseArgumentCaptor.capture());
        verify(dailyPlanRepository).save(dailyPlanArgumentCaptor.capture());

        Expense resultExpense = expenseArgumentCaptor.getValue();
        DailyPlan resultDailyPlan = dailyPlanArgumentCaptor.getValue();

        assertEquals(request.getExpenseTitle(), resultExpense.getTitle());
        assertEquals(request.getExpenseMemo(), resultExpense.getMemo());
        assertEquals(request.getCategoryName(), resultExpense.getCategory().getName());
        assertEquals(request.getExpenseDate(), resultExpense.getDate());

        assertEquals(request.getExpenseCost(), resultExpense.getCost());
        assertEquals(actualSum, resultDailyPlan.getTotalCost());
    }

    @Test
    @Transactional
    void 정상_수정_전체_금액_감소() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.of(expense));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(category));
        given(dailyPlanRepository.findDailyPlanByDateAndMemberId(any(LocalDate.class), any(String.class)))
                .willReturn(Optional.of(dailyPlan));
        given(expenseRepository.save(any(Expense.class))).willReturn(expense);
        given(dailyPlanRepository.save(any(DailyPlan.class))).willReturn(dailyPlan);

        // when
        expenseService.updateExpense(member.getId(), request);

        // then
        verify(expenseRepository).save(expenseArgumentCaptor.capture());
        verify(dailyPlanRepository).save(dailyPlanArgumentCaptor.capture());

        Expense resultExpense = expenseArgumentCaptor.getValue();
        DailyPlan resultDailyPlan = dailyPlanArgumentCaptor.getValue();

        assertEquals(request.getExpenseCost(), resultExpense.getCost());
        assertEquals(actualSum, resultDailyPlan.getTotalCost());
    }

    @Test
    @Transactional
    void 아이디에_맞는_멤버가_없는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> {expenseService.updateExpense(member.getId(), request);})
                .isInstanceOf(ExpenseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @Transactional
    void 아이디에_맞는_소비기록이_없는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> {expenseService.updateExpense(member.getId(), request);})
                .isInstanceOf(ExpenseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPENSE_NOT_FOUND);
    }

    @Test
    @Transactional
    void 입력한_소비기록이_사용자의것이_아닌_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Member other = MemberFixture.MEMBER_TWO;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(other);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.of(expense));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> {expenseService.updateExpense(member.getId(), request);})
                .isInstanceOf(ExpenseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
    }

    @Test
    @Transactional
    void 이름에_맞는_카테고리가_없는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.of(expense));

        // when
        assertThatThrownBy(() -> {expenseService.updateExpense(member.getId(), request);})
                .isInstanceOf(ExpenseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @Transactional
    void 해당날짜에_목표가_없는_경우_실패() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        expense.setMember(member);
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(200L)
                .build();
        UpdateExpenseRequest request = new UpdateExpenseRequest(expense.getId(), "수정 제목", 50L,
                expense.getDate(), "수정 메모", category.getName());
        long diff = request.getExpenseCost() - expense.getCost();
        long actualSum = dailyPlan.getTotalCost() + diff;


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(expenseRepository.findExpenseByIdFetchMember(any(Long.class))).willReturn(Optional.of(expense));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(category));
        given(dailyPlanRepository.findDailyPlanByDateAndMemberId(any(LocalDate.class), any(String.class)))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> {expenseService.updateExpense(member.getId(), request);})
                .isInstanceOf(ExpenseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NO_DAILY_PLAN_GIVEN_DATE);
    }
}
