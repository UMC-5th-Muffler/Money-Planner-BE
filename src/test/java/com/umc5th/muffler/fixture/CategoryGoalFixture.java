package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.CategoryGoal;

public class CategoryGoalFixture {

    public static CategoryGoal create(){
        return CategoryGoal.builder()
                .id(1L)
                .category(CategoryFixture.CATEGORY_ONE)
                .budget(5000L)
                .build();
    }
}
