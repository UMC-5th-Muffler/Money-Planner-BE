package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public DailyExpenseDetailsResponse getDailyExpenseDetails(LocalDate date, Pageable pageable){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long dailyTotalCost = expenseRepository.calculateTotalCostByMemberAndDate(member, date);
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(member, date, pageable);
        List<Category> categoryList = categoryRepository.findAllByMember(member);

        DailyExpenseDetailsResponse response = ExpenseConverter.toDailyExpenseDetailsResponse(expenseList, categoryList, date, dailyTotalCost);

        return response;
    }

    public WeeklyExpenseDetailsResponse getWeeklyExpenseDetails(LocalDate startDate, LocalDate endDate, Pageable pageable){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long weeklyTotalCost = expenseRepository.calculateTotalCostByMemberAndDateBetween(member, startDate, endDate);
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate, pageable);
        List<Category> categoryList = categoryRepository.findAllByMember(member);

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.stream().collect(Collectors.groupingBy(Expense::getDate));

        // 일별로 dailyExpenseDetailsDtos(하루 간의 소비내역 정보) 생성
        List<WeeklyExpenseDetailsResponse.DailyExpenseDetailsDto> dailyExpenseDetailsDtos = expensesByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 날짜 순으로 정렬
                .map(entry -> {
                    LocalDate dailyDate = entry.getKey();
                    List<Expense> dailyExpenseList = entry.getValue();

                    // 일일 소비 총합
                    Long dailyTotalCost = expenseRepository.calculateTotalCostByMemberAndDate(member, dailyDate);

                    return ExpenseConverter.toDailyExpenseDetailDto(dailyExpenseList, dailyDate, dailyTotalCost);
                })
                .collect(Collectors.toList());

        // WeeklyExpenseDetailsResponse(일주일 간 소비 내역 정보) 생성
        WeeklyExpenseDetailsResponse response = ExpenseConverter.toWeeklyExpenseDetailsResponse(dailyExpenseDetailsDtos, expenseList, categoryList, startDate, endDate, weeklyTotalCost);

        return response;
    }

}
