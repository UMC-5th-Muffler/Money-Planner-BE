package com.umc5th.muffler.domain.routine.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.*;
import static java.time.DayOfWeek.MONDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.umc5th.muffler.domain.dailyplan.repository.JDBCDailyPlanRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.GoalTerm;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.RoutineAll;
import com.umc5th.muffler.domain.routine.dto.RoutineDetail;
import com.umc5th.muffler.domain.routine.dto.RoutineRequest;
import com.umc5th.muffler.domain.routine.dto.RoutineResponse;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.fixture.*;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import com.umc5th.muffler.global.util.DateTimeProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private GoalRepository goalRepository;
    @MockBean
    private JDBCDailyPlanRepository jdbcDailyPlanRepository;
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
        List<GoalTerm> mockGoalTerms = List.of(GoalTermFixture.create());

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(LocalDate.of(2024, 1, 8));
        when(goalRepository.findGoalsWithinDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(mockGoalTerms);

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(1)).save(any());
        verify(goalRepository).findGoalsWithinDateRange(any(), any());
        verify(jdbcDailyPlanRepository).updateTotalCostForDailyPlans(anyString(), anyList(), anyLong());

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
        List<GoalTerm> mockGoalTerms = List.of(GoalTermFixture.create());

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(dateTimeProvider.nowDate()).thenReturn(LocalDate.of(2024, 1, 1));
        when(goalRepository.findGoalsWithinDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(mockGoalTerms);

        routineService.create(expenseId, request);

        verify(routineRepository).save(routineCaptor.capture());
        verify(expenseRepository, times(1)).save(any());
        verify(goalRepository).findGoalsWithinDateRange(any(), any());
        verify(jdbcDailyPlanRepository).updateTotalCostForDailyPlans(anyString(), anyList(), anyLong());

        Routine saved = routineCaptor.getValue();
        assertThat(saved.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(saved.getType()).isEqualTo(request.getType());
        assertThat(saved.getMonthlyRepeatDay()).isEqualTo(Integer.parseInt(request.getMonthlyRepeatDay()));
    }

    @Test
    void 루틴_전체조회_루틴존재() {

        String memberId = "1";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        Member mockMember = MemberFixture.MEMBER_ONE;
        Routine mockRoutine = RoutineFixture.ROUTINE_ONE;
        List<Routine> routineList = List.of(mockRoutine);
        Slice<Routine> routineSlice = new SliceImpl<>(routineList, pageable, false);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findRoutinesWithWeeklyDetails(memberId, null, pageable)).thenReturn(routineSlice);

        RoutineResponse response = routineService.getAllRoutines(pageable, null, memberId);

        assertNotNull(response);
        RoutineAll responseRoutine = response.getRoutineList().get(0);
        assertEquals(mockRoutine.getId(), responseRoutine.getRoutineId());
        assertEquals(mockRoutine.getTitle(), responseRoutine.getRoutineTitle());
        assertEquals(mockRoutine.getCost(), responseRoutine.getRoutineCost());
        assertEquals(mockRoutine.getCategory().getIcon(), responseRoutine.getCategoryIcon());
        assertEquals(mockRoutine.getMonthlyRepeatDay(), responseRoutine.getMonthlyRepeatDay());
        assertEquals(routineSlice.hasNext(), response.isHasNext());

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findRoutinesWithWeeklyDetails(memberId, null, pageable);
    }

    @Test
    void 루틴_전체조회_루틴존재X() {
        String memberId = "1";
        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        Member mockMember = MemberFixture.MEMBER_ONE;
        List<Routine> routineList = new ArrayList<>();
        Slice<Routine> routineSlice = new SliceImpl<>(routineList, pageable, false);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findRoutinesWithWeeklyDetails(memberId, null, pageable)).thenReturn(routineSlice);

        RoutineResponse response = routineService.getAllRoutines(pageable, null, memberId);

        assertNotNull(response);
        assertTrue(response.getRoutineList().isEmpty());
        assertEquals(routineSlice.hasNext(), response.isHasNext());

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findRoutinesWithWeeklyDetails(memberId, null, pageable);
    }

    @Test
    void 루틴_전체조회_다음페이지_존재() {
        String memberId = "1";
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        Member mockMember = MemberFixture.MEMBER_ONE;
        List<Routine> routineList = RoutineFixture.createList(6, LocalDate.of(2024, 1, 1));
        Slice<Routine> routineSlice = new SliceImpl<>(routineList, pageable, true);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findRoutinesWithWeeklyDetails(memberId, null, pageable)).thenReturn(routineSlice);

        RoutineResponse response = routineService.getAllRoutines(pageable, null, memberId);

        assertNotNull(response);
        assertEquals(routineSlice.hasNext(), response.isHasNext());

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findRoutinesWithWeeklyDetails(memberId, null, pageable);
    }

    @Test
    void 루틴_전체조회_사용자_존재X() {
        String memberId = "1";
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.getAllRoutines(pageable, null, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 루틴_상세조회_루틴존재O() {
        String memberId = "1";
        Long routineId = 1L;
        Member mockMember = MemberFixture.MEMBER_ONE;
        Routine mockRoutine = RoutineFixture.ROUTINE_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findByIdAndMemberIdWithCategory(routineId, memberId)).thenReturn(Optional.of(mockRoutine));

        RoutineDetail response = routineService.getRoutine(memberId, routineId);

        assertNotNull(response);
        assertEquals(mockRoutine.getMemo(), response.getRoutineMemo());
        assertEquals(mockRoutine.getCategory().getName(), response.getCategoryName());

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findByIdAndMemberIdWithCategory(routineId, memberId);
    }

    @Test
    void 루틴_상세조회_루틴존재X() {
        String memberId = "1";
        Long routineId = 1L;
        Member mockMember = MemberFixture.MEMBER_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findByIdAndMemberIdWithCategory(routineId, memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.getRoutine(memberId, routineId))
                .isInstanceOf(RoutineException.class)
                .hasFieldOrPropertyWithValue("errorCode", ROUTINE_NOT_FOUND);

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findByIdAndMemberIdWithCategory(routineId, memberId);
    }


    @Test
    void 루틴삭제_성공() {
        String memberId = "1";
        Long routineId = 1L;

        Member mockMember = mock(Member.class);
        Routine mockRoutine = mock(Routine.class);

        when(mockMember.getId()).thenReturn(memberId);
        when(mockRoutine.getMember()).thenReturn(mockMember);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findByIdAndMemberId(routineId, memberId)).thenReturn(Optional.of(mockRoutine));

        routineService.delete(routineId, memberId);

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findByIdAndMemberId(routineId, memberId);
    }

    @Test
    void 루틴삭제_루틴존재X_실패() {
        String memberId = "1";
        Long routineId = 1L;
        Member mockMember = MemberFixture.MEMBER_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findByIdAndMemberId(routineId, memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.delete(routineId, memberId))
                .isInstanceOf(RoutineException.class)
                .hasFieldOrPropertyWithValue("errorCode", ROUTINE_NOT_FOUND);

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findByIdAndMemberId(routineId, memberId);
    }

    @Test
    void 루틴삭제_사용자_존재X_실패() {
        String memberId = "1";
        Long routineId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.delete(routineId, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);

        verify(memberRepository).findById(memberId);
    }

    @Test
    void 루틴삭제_사용자_권한X_실패() {
        String memberId = "1";
        Long routineId = 1L;

        Member mockMember = mock(Member.class);
        Routine mockRoutine = mock(Routine.class);

        when(mockMember.getId()).thenReturn(memberId);
        when(mockRoutine.getMember()).thenReturn(mock(Member.class));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(routineRepository.findByIdAndMemberId(routineId, mockMember.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.delete(routineId, memberId))
                .isInstanceOf(RoutineException.class)
                .hasFieldOrPropertyWithValue("errorCode", ROUTINE_NOT_FOUND);

        verify(memberRepository).findById(memberId);
        verify(routineRepository).findByIdAndMemberId(routineId, memberId);
    }
}
