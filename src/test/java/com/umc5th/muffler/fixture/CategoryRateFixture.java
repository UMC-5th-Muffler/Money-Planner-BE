package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.CategoryRate;
import com.umc5th.muffler.entity.constant.Level;

public class CategoryRateFixture {

    public static final CategoryRate CATEGORY_RATE_ONE = CategoryRate.builder()
            .id(1L)
            .categoryGoal(CategoryGoalFixture.CATEGORY_GOAL_ONE)
            .level(Level.MEDIUM)
            .build();

    public static final CategoryRate CATEGORY_RATE_TWO = CategoryRate.builder()
            .id(2L)
            .categoryGoal(CategoryGoalFixture.CATEGORY_GOAL_TWO)
            .level(Level.LOW)
            .build();
}
