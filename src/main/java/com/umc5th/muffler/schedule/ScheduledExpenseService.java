package com.umc5th.muffler.schedule;

import com.umc5th.muffler.domain.expense.repository.JDBCExpenseRepository;
import com.umc5th.muffler.domain.expense.repository.JDBCExpenseRepository.InsertExpenseEntity;
import com.umc5th.muffler.domain.goal.repository.JDBCDailyPlanBatchRepository;
import com.umc5th.muffler.domain.goal.repository.JDBCDailyPlanBatchRepository.UpdateTotalCost;
import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduledExpenseService {
    private final DateTimeProvider dateTimeProvider;
    private final RoutineRepository routineRepository;
    private final JDBCExpenseRepository jdbcExpenseRepository;
    private final JDBCDailyPlanBatchRepository jdbcDailyPlanBatchRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void saveExpenseWithRoutine() {
        LocalDate today = dateTimeProvider.nowDate();
        List<InsertableRoutine> insertableRoutines = routineRepository.findInsertableRoutines(today);
        List<InsertExpenseEntity> insertExpenses = insertableRoutines.stream()
                .map(insertableRoutine -> makeExpense(insertableRoutine, today))
                .collect(Collectors.toList());
        List<UpdateTotalCost> updateTotalCosts = toUpdateTotalCostList(insertableRoutines);

        jdbcExpenseRepository.saveAllExpense(insertExpenses);
        jdbcDailyPlanBatchRepository.saveTotalCosts(updateTotalCosts);
    }

    private List<UpdateTotalCost> toUpdateTotalCostList(List<InsertableRoutine> routines) {
        Map<Long, UpdateTotalCost> updateMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (InsertableRoutine routine : routines) {
            if (updateMap.containsKey(routine.getDailyPlanId())) {
                UpdateTotalCost updateTotalCost = updateMap.get(routine.getDailyPlanId());
                updateTotalCost.addCost(routine.getRoutineCost());
            } else {
                updateMap.put(routine.getDailyPlanId(),
                        new UpdateTotalCost(routine.getDailyPlanId(), routine.getRoutineCost() + routine.getDailyPlanTotalCost(), now));
            }
        }
        return updateMap.values().stream().collect(Collectors.toList());
    }

    private InsertExpenseEntity makeExpense(InsertableRoutine insertableRoutine, LocalDate today) {
        return InsertExpenseEntity.builder()
                .date(today)
                .cost(insertableRoutine.getRoutineCost())
                .title(insertableRoutine.getRoutineTitle())
                .memo(insertableRoutine.getRoutineMemo())
                .memberId(insertableRoutine.getMemberId())
                .categoryId(insertableRoutine.getCategoryId())
                .now(LocalDateTime.now())
                .build();
    }
}
