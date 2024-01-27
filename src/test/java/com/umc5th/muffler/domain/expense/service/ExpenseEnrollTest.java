package com.umc5th.muffler.domain.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.MemberFixture;
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

@ExtendWith(MockitoExtension.class)
public class ExpenseEnrollTest {
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
    public void 정상_입력() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        DailyPlan dailyPlan = DailyPlan.builder()
                .totalCost(0L)
                .build();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(category));
        given(dailyPlanRepository.findDailyPlanByDateAndMemberId(any(LocalDate.class), any(String.class)))
                .willReturn(Optional.of(dailyPlan));
        given(expenseRepository.save(any(Expense.class))).willReturn(expense);

        // given
        NewExpenseRequest req = new NewExpenseRequest(expense.getTitle(), expense.getCost(),
                expense.getDate(), null, category.getName());
        // when
        expenseService.enrollExpense(member.getId(), req);
        // then
        verify(expenseRepository).save(expenseArgumentCaptor.capture());
        verify(dailyPlanRepository).save(dailyPlanArgumentCaptor.capture());

        Expense expenseResult = expenseArgumentCaptor.getValue();
        DailyPlan dailyPlanResult = dailyPlanArgumentCaptor.getValue();
        assertEquals(req.getExpenseCost(), expenseResult.getCost());
        assertEquals(expense.getCost(), dailyPlanResult.getTotalCost());
    }

    @Test
    public void 등록_안된_카테고리의_경우() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;
        Goal goal = Goal.builder().build();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.empty());

        NewExpenseRequest req = new NewExpenseRequest(expense.getTitle(), expense.getCost(),
                expense.getDate(), null, category.getName());
        assertThrows(ExpenseException.class, () -> {
            NewExpenseResponse res = expenseService.enrollExpense(member.getId(), req);
        });
    }

    @Test
    public void 멤버가_등록_안된_경우() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        NewExpenseRequest req = new NewExpenseRequest(expense.getTitle(), expense.getCost(),
                expense.getDate(), null, category.getName());
        assertThrows(ExpenseException.class, () -> {
            NewExpenseResponse res = expenseService.enrollExpense(member.getId(), req);
        });
    }

    @Test
    public void 목표가_등록_안된_경우() {
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;
        Expense expense = ExpenseFixture.EXPENSE_ONE;

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(categoryRepository.findCategoryWithNameAndMemberId(any(String.class), any(String.class)))
                .willReturn(Optional.of(category));
        given(dailyPlanRepository.findDailyPlanByDateAndMemberId(any(LocalDate.class), any(String.class)))
                .willReturn(Optional.empty());

        NewExpenseRequest req = new NewExpenseRequest(expense.getTitle(), expense.getCost(),
                expense.getDate(), null, category.getName());
        assertThrows(ExpenseException.class, () -> {
            NewExpenseResponse res = expenseService.enrollExpense(member.getId(), req);
        });
    }
}
