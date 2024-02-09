package com.umc5th.muffler.domain.goal.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.goal.repository.JDBCGoalRepository.UpdateTotalCost;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.CategoryFixture;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JDBCGoalRepositoryBatchGoalInsertTest {
    @Autowired private JDBCGoalRepository jdbcGoalRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private GoalRepository goalRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void 정상_입력_테스트() {
        Member member = MemberFixture.create();
        member = memberRepository.save(member);

        Category category = CategoryFixture.create(member);
        category = categoryRepository.save(category);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 10);
        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member, startDate, endDate);
        goal = goalRepository.save(goal);

        List<UpdateTotalCost> updateTotalCosts = new ArrayList<>();
        List<DailyPlan> dailyPlans = goal.getDailyPlans();
        for (DailyPlan dailyPlan : dailyPlans){
            updateTotalCosts.add(new UpdateTotalCost(dailyPlan.getId(), 100L, LocalDateTime.now()));
        }
        int errorCount = jdbcGoalRepository.saveTotalCosts(updateTotalCosts);
        assertEquals(0, errorCount);
    }
}