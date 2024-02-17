package com.umc5th.muffler.domain.dailyplan.service;

import static com.umc5th.muffler.entity.QDailyPlan.dailyPlan;

import com.querydsl.core.Tuple;
import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.dto.DailyInfo;
import com.umc5th.muffler.domain.dailyplan.dto.GoalInfo;
import com.umc5th.muffler.domain.dailyplan.dto.HomeConverter;
import com.umc5th.muffler.domain.dailyplan.dto.InactiveDaily;
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
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public WholeCalendar getNowCalendar(String memberId) {
        LocalDate date = dateTimeProvider.nowDate();
        Goal activeGoal = goalRepository.findByDateBetween(date, memberId).orElse(null);

        if (activeGoal == null) {
            return getBasicCalendar(memberId, YearMonth.from(date));
        }

        return getGoalCalendar(memberId, activeGoal, YearMonth.from(date));
    }

    @Transactional(readOnly = true)
    public WholeCalendar getBasicCalendar(String memberId, YearMonth date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        List<Goal> inactiveGoals = goalRepository.findGoalsByYearMonth(member.getId(), date);
        List<DailyInfo> inactiveDailies = getInactiveDailies(inactiveGoals, date);

        return HomeConverter.toBasicCalendar(inactiveDailies);
    }

    @Transactional(readOnly = true)
    public WholeCalendar getDefaultGoalCalendar(String memberId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));

        // 오늘 날짜인 goalCalendar 반환 but 기간이 끝났을 시 목표 시작 지점인 goalCalendar 반환
        LocalDate now = dateTimeProvider.nowDate();
        YearMonth date = YearMonth.from(now);
        if (now.isBefore(goal.getStartDate()) || now.isAfter(goal.getEndDate())) {
            date = YearMonth.from(goal.getStartDate());
        }

        return getGoalCalendar(memberId, goal, date);
    }

    @Transactional(readOnly = true)
    public WholeCalendar getDateGoalCalendar(String memberId, Long goalId, YearMonth date) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));

        // 선택한 날짜가 선택한 goal 기간 밖일 때 BasicCalendar 반환
        LocalDate startOfMonth = date.atDay(1);
        LocalDate endOfMonth = date.atEndOfMonth();
        if (startOfMonth.isAfter(goal.getEndDate()) || endOfMonth.isBefore(goal.getStartDate())) {
            return getBasicCalendar(memberId, date);
        }

        return getGoalCalendar(memberId, goal, date);
    }

    @Transactional(readOnly = true)
    public WholeCalendar getCategoryCalendar(String memberId, Long goalId, YearMonth date, Long categoryId) {
        Goal activeGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(ErrorCode.GOAL_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        Long categoryTotalCost = expenseRepository.sumTotalCategoryCostByMemberAndDateBetween(
                memberId, categoryId, activeGoal.getStartDate(), activeGoal.getEndDate()).orElse(0L);
        Long categoryBudget = getCategoryBudget(activeGoal, category);

        List<DailyInfo> categoryDailies = getCategoryDailies(memberId, date, categoryId, activeGoal);

        List<Goal> inactiveGoals = goalRepository.findGoalsByYearMonth(member.getId(), date);
        inactiveGoals.remove(activeGoal);
        List<DailyInfo> inactiveDailies = getInactiveDailies(inactiveGoals, date);

        return HomeConverter.toCategoryCalendar(category, categoryTotalCost, categoryBudget, categoryDailies, inactiveDailies);
    }

    private WholeCalendar getGoalCalendar(String memberId, Goal activeGoal, YearMonth date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        LocalDate startDate = findStartDateWithinYearMonth(activeGoal, date);
        LocalDate endDate = findEndDateWithinYearMonth(activeGoal, date);

        GoalInfo goalInfo = getGoalInfo(activeGoal);
        List<DailyInfo> activeDailies = getActiveDailies(activeGoal, startDate, endDate);

        List<Goal> inactiveGoals = goalRepository.findGoalsByYearMonth(member.getId(), date);
        inactiveGoals.remove(activeGoal);
        List<DailyInfo> inactiveDailies = getInactiveDailies(inactiveGoals, date);

        return HomeConverter.toGoalCalendar(goalInfo, activeDailies, inactiveDailies);
    }

    private GoalInfo getGoalInfo(Goal activeGoal) {
        String memberId = activeGoal.getMember().getId();
        Long totalCost = expenseRepository.sumCostByMemberAndDateBetween(
                memberId, activeGoal.getStartDate(), activeGoal.getEndDate());

        return HomeConverter.toGoalInfo(activeGoal, totalCost);
    }

    private List<DailyInfo> getActiveDailies(Goal activeGoal, LocalDate startDate, LocalDate endDate) {
        List<DailyPlan> dailyPlans = dailyPlanRepository
                .findByGoalIdAndDateBetween(activeGoal.getId(), startDate, endDate);

        return HomeConverter.toActiveDailies(dailyPlans);
    }

    private List<DailyInfo> getInactiveDailies(List<Goal> inactiveGoals, YearMonth date) {
        List<DailyInfo> inactiveDailies = new ArrayList<>();
        for (Goal goal : inactiveGoals) {
            LocalDate startDate = findStartDateWithinYearMonth(goal, date);
            LocalDate endDate = findEndDateWithinYearMonth(goal, date);

            List<Tuple> rates = dailyPlanRepository.findDateAndRateByGoalAndDateRange(goal.getId(), startDate, endDate);
            rates.stream()
                    .forEach(tuple -> {
                        inactiveDailies.add((DailyInfo)
                                new InactiveDaily(tuple.get(dailyPlan.date), tuple.get(dailyPlan.rate)));
                    });
        }
        return inactiveDailies;
    }

    private Long getCategoryBudget(Goal goal, Category category) {
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        for (CategoryGoal categoryGoal : categoryGoals) {
            if (Objects.equals(categoryGoal.getCategory().getId(), category.getId())) {
                return categoryGoal.getBudget();
            }
        }
        return null;
    }

    private List<DailyInfo> getCategoryDailies(String memberId, YearMonth date, Long categoryId, Goal goal) {
        // 선택한 날짜가 선택한 goal 기간 안일 때만 존재
        LocalDate startOfMonth = date.atDay(1);
        LocalDate endOfMonth = date.atEndOfMonth();
        if (!startOfMonth.isAfter(goal.getEndDate()) && !endOfMonth.isBefore(goal.getStartDate())) {
            LocalDate startDate = findStartDateWithinYearMonth(goal, date);
            LocalDate endDate = findEndDateWithinYearMonth(goal, date);

            Map<LocalDate, List<Expense>> expenses = expenseRepository
                    .findByMemberAndCategoryAndDateRangeGroupedByDate(memberId, categoryId, startDate, endDate);
            return HomeConverter.toCategoryDaily(expenses, startDate, endDate);
        }
        return new ArrayList<>();
    }

    private LocalDate findStartDateWithinYearMonth(Goal goal, YearMonth date) {
        LocalDate startOfMonth = date.atDay(1);
        if (goal.getStartDate().isBefore(startOfMonth)) {
            return startOfMonth;
        }
        return goal.getStartDate();
    }

    private LocalDate findEndDateWithinYearMonth(Goal goal, YearMonth date) {
        LocalDate endOfMonth = date.atEndOfMonth();
        if (goal.getEndDate().isAfter(endOfMonth)) {
            return endOfMonth;
        }
        return goal.getEndDate();
    }
}
