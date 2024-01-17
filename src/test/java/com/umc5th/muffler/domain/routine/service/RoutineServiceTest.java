package com.umc5th.muffler.domain.routine.service;

import static java.time.DayOfWeek.MONDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.routine.dto.RoutineRequest;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.fixture.ExpenseFixture;
import com.umc5th.muffler.fixture.RoutineRequestFixture;
import com.umc5th.muffler.global.util.DateTimeProvider;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class RoutineServiceTest {

    @Autowired
    private RoutineService routineService;
    @MockBean
    private RoutineRepository routineRepository;
    @MockBean
    private ExpenseRepository expenseRepository;
    @MockBean
    private DateTimeProvider dateTimeProvider;
    @Captor
    private ArgumentCaptor<Routine> routineCaptor;


    // TODO : 루틴 관련 경계값 테스트 & 예외 테스트 필요 ...
    @Test
    void 과거소비등록X_Weekly루틴_등록_성공() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Expense expense = ExpenseFixture.create(date);
        RoutineRequest request = RoutineRequestFixture.createWeekly();
        Long expenseId = expense.getId();

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(date);

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(0)).save(any());

        Routine saved = routineCaptor.getValue();
        assertThat(saved.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(saved.getType()).isEqualTo(request.getType());
        assertThat(saved.getWeeklyRepeatDays()).extracting("dayOfWeek").containsExactly(MONDAY);
        assertThat(saved.getWeeklyTerm()).isEqualTo(Integer.parseInt(request.getWeeklyTerm()));
    }

    @Test
    void 과거소비등록O_Weekly루틴_등록_성공() {
        Expense expense = ExpenseFixture.create(LocalDate.of(2024, 1, 1));
        RoutineRequest request = RoutineRequestFixture.createWeekly();
        Long expenseId = expense.getId();

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(LocalDate.of(2024, 1, 8));

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(1)).save(any());

        Routine saved = routineCaptor.getValue();
        assertThat(saved.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(saved.getType()).isEqualTo(request.getType());
        assertThat(saved.getWeeklyRepeatDays()).extracting("dayOfWeek").containsExactly(MONDAY);
        assertThat(saved.getWeeklyTerm()).isEqualTo(Integer.parseInt(request.getWeeklyTerm()));
    }

    @Test
    void 과거소비등록X_Monthly루틴_등록_성공() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Expense expense = ExpenseFixture.create(date);
        RoutineRequest request = RoutineRequestFixture.createMonthly();
        Long expenseId = expense.getId();

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(date);

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(0)).save(any());

        Routine saved = routineCaptor.getValue();
        assertThat(saved.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(saved.getType()).isEqualTo(request.getType());
        assertThat(saved.getMonthlyRepeatDay()).isEqualTo(Integer.parseInt(request.getMonthlyRepeatDay()));
    }

    @Test
    void 과거소비등록O_Monthly루틴_등록_성공() {
        Expense expense = ExpenseFixture.create(LocalDate.of(2023, 12, 1));
        RoutineRequest request = RoutineRequestFixture.createMonthly();
        Long expenseId = expense.getId();

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(LocalDate.of(2024, 1, 1));

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(1)).save(any());

        Routine saved = routineCaptor.getValue();
        assertThat(saved.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(saved.getType()).isEqualTo(request.getType());
        assertThat(saved.getMonthlyRepeatDay()).isEqualTo(Integer.parseInt(request.getMonthlyRepeatDay()));
    }

}
