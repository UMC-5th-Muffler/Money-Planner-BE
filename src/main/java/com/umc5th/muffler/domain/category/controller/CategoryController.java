package com.umc5th.muffler.domain.category.controller;

import com.umc5th.muffler.domain.category.dto.CategoryDto;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryRequest;
import com.umc5th.muffler.domain.category.service.CategoryService;
import com.umc5th.muffler.global.response.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public Response<CategoryDto> createCategory(@RequestBody @Valid NewCategoryRequest request) {
        CategoryDto newCategory = categoryService.createNewCategory(request.getMemberId(), request);
        return Response.success(newCategory);
    }
    @PatchMapping
    private Response<Void> updateCategory(@RequestBody @Valid UpdateCategoryRequest request) {
        categoryService.updateCategory(request.getMemberId(), request);
        return Response.success();
    }

    @DeleteMapping("/{categoryId}")
    private Response<Void> deleteCategory(Principal principal, @PathVariable("categoryId") Long categoryId) {
        categoryService.deactivateCategory(principal.getName(), categoryId);
        return Response.success();
    }
}
