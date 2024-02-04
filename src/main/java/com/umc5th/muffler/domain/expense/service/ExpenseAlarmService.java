package com.umc5th.muffler.domain.expense.service;

import static com.umc5th.muffler.entity.constant.ExpenseAlarm.CATEGORY;
import static com.umc5th.muffler.entity.constant.ExpenseAlarm.DAILY;
import static com.umc5th.muffler.entity.constant.ExpenseAlarm.TOTAL;

import com.umc5th.muffler.domain.expense.dto.AlarmControlDTO;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseAlarmService {

    private final CategoryGoalRepository categoryGoalRepository;
    private final ExpenseRepository expenseRepository;

    public List<AlarmControlDTO> getAlarms(DailyPlan dailyPlan, Category category, Long expenditure) {
        List<AlarmControlDTO> alarms = new ArrayList<>();
        setDailyAlarm(dailyPlan, expenditure, alarms);
        setCategoryAlarm(category, dailyPlan.getGoal(), expenditure, alarms);
        setGoalAlarm(dailyPlan.getGoal(), expenditure, alarms);
        return alarms;
    }

    private void setDailyAlarm(DailyPlan dailyPlan, Long expenditure, List<AlarmControlDTO> alarms) {
        if (dailyPlan.isPossibleToAlarm(expenditure)) {
            alarms.add(new AlarmControlDTO(
                    DAILY, dailyPlan.getBudget(),
                    dailyPlan.getTotalCost() + expenditure - dailyPlan.getBudget()));
        }
    }

    private void setCategoryAlarm(Category category, Goal goal, Long expenditure, List<AlarmControlDTO> alarms) {
        categoryGoalRepository.findByGoalIdAndCategoryId(goal.getId(), category.getId())
                .ifPresent(categoryGoal -> {
                    Long totalCategoryExpense = expenseRepository.sumCategoryExpenseWithinGoal(goal.getMember().getId(), category, goal);

                    if (categoryGoal.isPossibleToAlarm(totalCategoryExpense, expenditure)) {
                        alarms.add(new AlarmControlDTO(
                                CATEGORY, categoryGoal.getBudget(),
                                totalCategoryExpense + expenditure - categoryGoal.getBudget()));
                    }
                });
    }

    private void setGoalAlarm(Goal goal, Long expenditure, List<AlarmControlDTO> alarms) {
        Long totalGoalExpense = expenseRepository.sumCostByMemberAndDateBetween(goal.getMember().getId(), goal.getStartDate(), goal.getEndDate());

        if (goal.isPossibleToAlarm(totalGoalExpense, expenditure)) {
            alarms.add(new AlarmControlDTO(
                    TOTAL, goal.getTotalBudget(),
                    totalGoalExpense + expenditure - goal.getTotalBudget()));
        }
    }

    public List<AlarmControlDTO> getDailyAlarm(DailyPlan dailyPlan, Long expenditure) {
        List<AlarmControlDTO> alarm = new ArrayList<>();
        setDailyAlarm(dailyPlan, expenditure, alarm);
        return alarm;
    }
}
