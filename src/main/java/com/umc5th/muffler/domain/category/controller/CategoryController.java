package com.umc5th.muffler.domain.category.controller;

import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.service.CategoryService;
import com.umc5th.muffler.global.response.Response;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/")
    public Response<CategoryDto> createCategory(@RequestBody @Valid NewCategoryRequest request) {
        Long memberId = 1L; // 회원 관리에 따라 달라질 예정임.
        CategoryDto newCategory = categoryService.createNewCategory(memberId, request);
        return Response.success(newCategory);
    }
}
