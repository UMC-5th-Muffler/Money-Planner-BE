package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.CategoryRate;
import com.umc5th.muffler.entity.constant.Level;

public class CategoryRateFixture {

    public static final CategoryRate CATEGORY_RATE_ONE = CategoryRate.builder()
            .categoryGoal(CategoryGoalFixture.create())
            .level(Level.MEDIUM)
            .build();
}
