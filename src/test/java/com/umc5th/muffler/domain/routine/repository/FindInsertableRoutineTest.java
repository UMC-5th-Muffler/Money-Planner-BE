package com.umc5th.muffler.domain.routine.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.fixture.RoutineFixture;
import com.umc5th.muffler.fixture.WeeklyRepeatDayFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class FindInsertableRoutineTest {
    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Test
    @Transactional
    void 성공_1주반복() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 8));

        // then
        assertEquals(1, routines.size());
    }
    @Test
    @Transactional
    void 성공_1주반복_종료날짜없음() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), null);
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 2, 5));

        // then
        assertEquals(1, routines.size());
    }
    @Test
    @Transactional
    void 실패_1주반복_요일이다름() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), null);
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 2, 7));

        // then
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 실패_목표시작일전임() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), null);
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 2, 4));

        // then
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 실패_목표종료일이지남() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), null);
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 2, 5));

        // then
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 실패_루틴시작일전임() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 2, 7), null);
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 8));

        // then
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 실패_루틴종료일후임() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 15));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 22));

        // then
        assertEquals(0, routines.size());
    }
    

    @Test
    @Transactional
    void 실패_2주반복() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerTwoWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 8));

        // then
        for (InsertableRoutine insertableRoutine : routines) {
            System.out.println(insertableRoutine.getRoutineId());
        }
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 성공_2주반복() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerTwoWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 15));

        // then
        for (InsertableRoutine insertableRoutine : routines) {
            System.out.println(insertableRoutine.getRoutineId());
        }
        assertEquals(1, routines.size());
    }

    @Test
    @Transactional
    void 성공_2주반복_첫째주_이후_요일_등록() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerTwoWeek(member, category,
                LocalDate.of(2024, 1, 3), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        WeeklyRepeatDay repeatDay2 = WeeklyRepeatDayFixture.of(routine, DayOfWeek.WEDNESDAY);
        WeeklyRepeatDay repeatDay3 = WeeklyRepeatDayFixture.of(routine, DayOfWeek.SATURDAY);
        routine.addRepeatDay(repeatDay);
        routine.addRepeatDay(repeatDay2);
        routine.addRepeatDay(repeatDay3);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines_one = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 8));
        List<InsertableRoutine> routines_two = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 6));
        List<InsertableRoutine> routines_three = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 20));
        // then
        assertEquals(0, routines_one.size());
        assertEquals(1, routines_two.size());
        assertEquals(1, routines_three.size());
    }

    @Test
    @Transactional
    void 성공_3주반복() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerThreeWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 22));

        // then
        for (InsertableRoutine insertableRoutine : routines) {
            System.out.println(insertableRoutine.getRoutineId());
        }
        assertEquals(1, routines.size());
    }

    @Test
    @Transactional
    void 실패_3주반복() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerThreeWeek(member, category,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        WeeklyRepeatDay repeatDay = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine.addRepeatDay(repeatDay);
        routineRepository.save(routine);

        Routine routine2 = RoutineFixture.routinePerThreeWeek(member, category,
                LocalDate.of(2024, 1, 8), LocalDate.of(2024, 2, 5));
        WeeklyRepeatDay repeatDay2 = WeeklyRepeatDayFixture.of(routine, DayOfWeek.MONDAY);
        routine2.addRepeatDay(repeatDay2);
        routineRepository.save(routine2);


        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 15));

        // then
        for (InsertableRoutine insertableRoutine : routines) {
            System.out.println(insertableRoutine.getRoutineId());
        }
        assertEquals(0, routines.size());
    }

    @Test
    @Transactional
    void 성공_월별_일자() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routinePerMonth(member, category,
                LocalDate.of(2024, 1, 1), null, 10);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 10));

        // then
        assertEquals(1, routines.size());
    }

    @Test
    @Transactional
    void 성공_월별_마지막() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routineLastDayOfMonth(member, category,
                LocalDate.of(2024, 1, 1), null);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 1, 31));

        // then
        assertEquals(1, routines.size());
    }

    @Test
    @Transactional
    void 성공_월별_첫번째_날() {
        // given
        Member member = MemberFixture.MEMBER_ONE;
        Category category = CategoryFixture.CATEGORY_ONE;

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Routine routine = RoutineFixture.routineFirstDayOfMonth(member, category,
                LocalDate.of(2024, 1, 1), null);
        routineRepository.save(routine);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
        goalRepository.save(goal);

        // when
        List<InsertableRoutine> routines = this.routineRepository.findInsertableRoutines(LocalDate.of(2024, 2, 1));

        // then
        assertEquals(1, routines.size());
    }
}