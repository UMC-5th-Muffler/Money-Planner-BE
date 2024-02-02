package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;

public class CategoryFixture {
    public static final Category CATEGORY_ONE = Category.builder()
            .id(1L)
            .name("카테고리1")
            .icon("ICON")
            .status(Status.ACTIVE)
            .priority(1L)
            .isVisible(true)
            .build();
    public static final Category CATEGORY_TWO = Category.builder()
            .id(2L)
            .name("카테고리2")
            .icon("ICON")
            .status(Status.ACTIVE)
            .priority(2L)
            .isVisible(true)
            .build();
    public static final Category CATEGORY_THREE = Category.builder()
            .id(3L)
            .name("카테고리3")
            .icon("ICON")
            .status(Status.INACTIVE)
            .priority(3L)
            .isVisible(true)
            .build();
    public static final Category CATEGORY_FOUR = Category.builder()
            .id(4L)
            .name("카테고리4")
            .icon("ICON")
            .status(Status.ACTIVE)
            .priority(4L)
            .isVisible(false)
            .build();
    public static Category create(Member member) {
        return Category.builder()
                .name("카테고리")
                .icon("icon")
                .member(member)
                .status(Status.ACTIVE)
                .build();
    }
}