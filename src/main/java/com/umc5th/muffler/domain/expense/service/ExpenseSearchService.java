package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.DailyExpensesDto;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.SearchResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseSearchService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;

    public SearchResponse searchExpense(String memberId, String searchKeyword, int size, String order, LocalDate lastDate, Long lastExpenseId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Slice<Expense> expenses = expenseRepository.findByMemberAndTitleContaining(memberId, searchKeyword, lastDate, lastExpenseId, size, order);

        Map<LocalDate, List<Expense>> expensesByDate = expenses.getContent().stream()
                .collect(Collectors.groupingBy(Expense::getDate, LinkedHashMap::new, Collectors.toList()));

        List<DailyExpensesDto> dailyExpensesDtos = ExpenseConverter.toDailyExpensesList(expensesByDate);
        return ExpenseConverter.toSearchResponse(dailyExpensesDtos, expenses.hasNext());
    }
}
