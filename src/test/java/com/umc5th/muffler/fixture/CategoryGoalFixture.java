package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.CategoryGoal;

public class CategoryGoalFixture {

    public static final CategoryGoal CATEGORY_GOAL_ONE = CategoryGoal.builder()
                .id(1L)
                .category(CategoryFixture.CATEGORY_ONE)
                .budget(5000L)
                .build();

    public static final CategoryGoal CATEGORY_GOAL_TWO = CategoryGoal.builder()
            .id(2L)
            .category(CategoryFixture.CATEGORY_TWO)
            .budget(5000L)
            .build();
}
