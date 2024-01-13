package com.umc5th.muffler.domain.category.service;

import static org.junit.jupiter.api.Assertions.*;

import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.global.response.exception.CategoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceTest {
    private final CategoryService categoryService;

    @Autowired
    public CategoryServiceTest(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Test
    @Transactional
    public void 정상실행() throws CategoryException {
        //given
        Long memberId = 1L;
        NewCategoryRequest request = new NewCategoryRequest("카테고리0");
        // when
        CategoryDto newCategory = categoryService.createNewCategory(memberId, request);
        //then
        assertEquals(newCategory.getName(), request.getCategoryName());
    }

    @Test
    @Transactional
    public void 중복_이름() throws CategoryException {
        // given
        Long memberId = 1L;
        NewCategoryRequest request = new NewCategoryRequest("카테고리");
        // when

        // then
        Assertions.assertThrows(CategoryException.class, () -> {
            CategoryDto newCategory = categoryService.createNewCategory(memberId, request);
        });
    }

    @Test
    @Transactional
    public void 사용자_아이디가_없는경우() throws CategoryException {
        //given
        Long memberId = 1000L;
        NewCategoryRequest request = new NewCategoryRequest("카테고리1");
        // when
        Assertions.assertThrows(CategoryException.class, () -> {
            CategoryDto newCategory = categoryService.createNewCategory(memberId, request);
        });
        //then
    }

}