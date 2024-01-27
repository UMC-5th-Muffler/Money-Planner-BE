package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsDto;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;

    public DailyExpenseDetailsResponse getDailyExpenseDetails(LocalDate date, Pageable pageable){
        String memberId = "1"; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long dailyTotalCost = expenseRepository.calculateTotalCostByMemberAndDate(member, date);
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(member, date, pageable);
        List<Category> categoryList = member.getCategories();

        DailyExpenseDetailsResponse response = ExpenseConverter.toDailyExpenseDetailsResponse(expenseList, categoryList, date, dailyTotalCost);

        return response;
    }

    public WeeklyExpenseDetailsResponse getWeeklyExpenseDetails(LocalDate date, Pageable pageable){
        String memberId = "1"; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 해당 주 월요일 날짜
        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 해당 주 일요일 날짜
        LocalDate endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Long weeklyTotalCost = expenseRepository.calculateTotalCostByMemberAndDateBetween(member, startDate, endDate);
        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate, pageable);
        List<Category> categoryList = member.getCategories();

        // 일별로 Expense 그룹화
        Map<LocalDate, List<Expense>> expensesByDate = expenseList.stream().collect(Collectors.groupingBy(Expense::getDate));
        Map<LocalDate, Long> dailyTotalCostMap = expensesByDate.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> expenseRepository.calculateTotalCostByMemberAndDate(member, entry.getKey())
                ));

        List<DailyExpenseDetailsDto> dailyExpenseDetailsDtos = ExpenseConverter.toDailyExpenseDetailsResponse(expensesByDate, dailyTotalCostMap);
        WeeklyExpenseDetailsResponse response = ExpenseConverter.toWeeklyExpenseDetailsResponse(dailyExpenseDetailsDtos, expenseList, categoryList, startDate, endDate, weeklyTotalCost);

        return response;
    }
}
