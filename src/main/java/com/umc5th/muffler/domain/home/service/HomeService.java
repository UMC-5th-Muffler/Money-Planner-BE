package com.umc5th.muffler.domain.home.service;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.home.dto.HomeConverter;
import com.umc5th.muffler.domain.home.dto.WholeCalendarResponse;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;

    public WholeCalendarResponse getWholeCalendarInfos(LocalDate date) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<Goal> goal = goalRepository.findByDateBetween(date, memberId);

        WholeCalendarResponse response;

        if(goal.isPresent()) {
            Goal actualGoal = goal.get();
            LocalDate startDate = actualGoal.getStartDate();
            LocalDate endDate = actualGoal.getEndDate();

            Long totalCost = expenseRepository.calculateTotalCostByMemberAndDateBetween(member, startDate, endDate);
            List<Category> categoryList = expenseRepository.findDistinctCategoriesBetweenDates(member, startDate, endDate);
            List<Long> dailyBudgetList = actualGoal.getDailyPlans().stream()
                    .map(DailyPlan::getBudget)
                    .collect(Collectors.toList());
//            List<Long> dailyTotalCostList = expenseRepository.calculateDailyTotalCostList(member.getId(), date, startDate, endDate);
            List<Long> dailyTotalCostList = calculateDailyTotalCostList(member, startDate, endDate);
            // TODO: List<Level> dailyRate 추가

            response = HomeConverter.toWholeCalendar(date, actualGoal, totalCost, categoryList, dailyBudgetList, dailyTotalCostList);
        } else {
            response= new WholeCalendarResponse();
        }

        return response;
    }

    private List<Long> calculateDailyTotalCostList(Member member, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findAllByMemberAndDateBetween(member, startDate, endDate);

        Map<LocalDate, Long> dailyExpenseMap = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate, Collectors.summingLong(Expense::getCost)));

        List<Long> dailyTotalCostList = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> dailyExpenseMap.getOrDefault(d, 0L))
                .collect(Collectors.toList());

        return dailyTotalCostList;
    }
}
