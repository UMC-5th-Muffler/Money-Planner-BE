package com.umc5th.muffler.domain.expense.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.repository.JDBCExpenseRepository.InsertExpenseEntity;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class JDBCExpenseRepositoryBatchSaveTest {
    @Autowired
    private JDBCExpenseRepository jdbcExpenseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Test
    @Transactional
    void 정상삽입_배치사이즈_보다작은경우() {
        final Long num = 5L;
        Member member = MemberFixture.create();
        Category category = CategoryFixture.create(member);
        category.setMember(member);

        member = memberRepository.save(member);
        category = categoryRepository.save(category);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 3));
        goalRepository.save(goal);

        List<InsertExpenseEntity> insertableRoutines = new ArrayList<>();
        for (int i = 0 ; i < num; i++) {
            insertableRoutines.add(
                    InsertExpenseEntity
                            .builder()
                            .title("test")
                            .memo("memo")
                            .categoryId(category.getId())
                            .memberId(member.getId())
                            .cost(1L)
                            .date(LocalDate.of(2024, 1, 2))
                            .build()
            );
        }
        int result = jdbcExpenseRepository.saveAllExpense(insertableRoutines);
        assertEquals(insertableRoutines.size(), result);
    }
}