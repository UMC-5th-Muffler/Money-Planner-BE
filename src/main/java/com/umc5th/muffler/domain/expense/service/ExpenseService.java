package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;


    @Transactional
    public NewExpenseResponse enrollExpense(NewExpenseRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        Goal goal = goalRepository.findByDateBetween(request.getExpenseDate(), request.getUserId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));

        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);
        expense = expenseRepository.save(expense);
        return new NewExpenseResponse(expense.getId(), expense.getCost());
    }

    public SearchResponse searchExpense(String memberId, String searchKeyword, int page, int size, String sortDirection) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction,"date").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        PageRequest pageable = PageRequest.of(page, size, sort);
        Slice<Expense> expenses = expenseRepository.findByMemberAndTitleContaining(member, searchKeyword, pageable);

        Comparator<LocalDate> comparator = sortDirection.equalsIgnoreCase("DESC") ? Comparator.reverseOrder() : Comparator.naturalOrder();
        Map<LocalDate, List<Expense>> expensesByDate = expenses.getContent().stream()
                .collect(Collectors.groupingBy(
                        Expense::getDate,
                        () -> new TreeMap<>(comparator),
                        Collectors.toList()
                ));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toSearch(expensesByDate);
        return ExpenseConverter.toSearchResponse(dailyExpensesDtos, expenses.hasNext());
    }
}
