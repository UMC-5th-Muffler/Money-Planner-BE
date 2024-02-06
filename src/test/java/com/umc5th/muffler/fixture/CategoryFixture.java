package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.CategoryType;
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
            .type(CategoryType.CUSTOM)
            .status(Status.ACTIVE)
            .priority(2L)
            .isVisible(true)
            .build();
    public static final Category CUSTOM_CATEGORY_ONE = Category.builder()
            .id(1L)
            .name("카테고리1")
            .icon("ICON")
            .type(CategoryType.CUSTOM)
            .status(Status.ACTIVE)
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
    public static final Category DEFAULT_CATEGORY_FOUR = Category.builder()
            .id(4L)
            .name("카테고리4")
            .icon("ICON")
            .type(CategoryType.DEFAULT)
            .status(Status.ACTIVE)
            .build();
    public static final Category ETC_CATEGORY = Category.builder()
            .id(5L)
            .name(Category.ETC_CATEGORY_NAME)
            .icon("ICON")
            .type(CategoryType.DEFAULT)
            .status(Status.ACTIVE)
            .build();
    public static final Category INACTIVE_CATEGORY_SIX = Category.builder()
            .id(6L)
            .name("카테고리6")
            .icon("ICON")
            .type(CategoryType.CUSTOM)
            .status(Status.INACTIVE)
            .build();

    public static Category createSameNamedDifferentCategory(Category category) {
        return Category.builder()
                .id(category.getId() + 100L)
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .priority(category.getPriority())
                .isVisible(category.getIsVisible())
                .status(category.getStatus())
                .member(category.getMember())
                .build();
    }
}