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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;

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
