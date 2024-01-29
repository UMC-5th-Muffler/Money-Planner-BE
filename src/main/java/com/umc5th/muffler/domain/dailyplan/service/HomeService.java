package com.umc5th.muffler.domain.dailyplan.service;

import com.mysema.commons.lang.Pair;
import com.umc5th.muffler.domain.category.dto.CategoryConverter;
import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.dto.ActiveGoalResponse;
import com.umc5th.muffler.domain.dailyplan.dto.CategoryCalendar;
import com.umc5th.muffler.domain.dailyplan.dto.GoalDailyInfo;
import com.umc5th.muffler.domain.dailyplan.dto.HomeConverter;
import com.umc5th.muffler.domain.dailyplan.dto.InactiveGoalInfo;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendar;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final DateTimeProvider dateTimeProvider;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public WholeCalendar getBasicCalendar(String memberId, YearMonth yearMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        List<Goal> inactiveGoals = goalRepository.findGoalsByYearMonth(member.getId(), yearMonth);
        List<InactiveGoalInfo> inactiveGoalsResponse = getInactiveGoalsResponse(inactiveGoals, yearMonth);

        List<Category> categoryFilters = getCategoryFilters(member);

        return HomeConverter.toBasicCalendarResponse(inactiveGoalsResponse, categoryFilters);
    }

    private List<InactiveGoalInfo> getInactiveGoalsResponse(List<Goal> inactiveGoals, YearMonth yearMonth) {
        Map<Long, List<Rate>> goalRates = new LinkedHashMap<>();
        Map<Long, Pair<LocalDate, LocalDate>> goalDates = new LinkedHashMap<>();

        for (Goal goal : inactiveGoals) {
            LocalDate startDate = findStartDateWithinYearMonth(goal, yearMonth);
            LocalDate endDate = findEndDateWithinYearMonth(goal, yearMonth);

            List<Rate> rates = dailyPlanRepository.findRatesByGoalAndDateRange(goal.getId(), startDate, endDate);
            goalRates.put(goal.getId(), rates);
            goalDates.put(goal.getId(), Pair.of(startDate, endDate));
        }
        return HomeConverter.toInactiveGoalsResponse(inactiveGoals, goalRates, goalDates);
    }

    private static List<Category> getCategoryFilters(Member member) {
        return member.getCategories().stream()
                .filter(category -> category.getStatus().isActive() && category.getIsVisible())
                .sorted(Comparator.comparingLong(Category::getPriority))
                .collect(Collectors.toList());
    }

    private LocalDate findStartDateWithinYearMonth(Goal goal, YearMonth yearMonth) {
        LocalDate startOfMonth = yearMonth.atDay(1);
        if (goal.getStartDate().isBefore(startOfMonth)) {
            return startOfMonth;
        }
        return goal.getStartDate();
    }

    private LocalDate findEndDateWithinYearMonth(Goal goal, YearMonth yearMonth) {
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        if (goal.getEndDate().isAfter(endOfMonth)) {
            return endOfMonth;
        }
        return goal.getEndDate();
    }
}
