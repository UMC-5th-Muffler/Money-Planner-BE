package com.umc5th.muffler.domain.category.controller;

import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.DeleteCategoryResponse;
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
    public Response<NewCategoryResponse> createCategory(Principal principal, @RequestBody @Valid NewCategoryRequest request) {
        NewCategoryResponse newCategory = categoryService.createNewCategory(principal.getName(), request);
        return Response.success(newCategory);
    }
    @PatchMapping
    private Response<Void> updateCategory(Principal principal, @RequestBody @Valid UpdateCategoryRequest request) {
        categoryService.updateCategory(principal.getName(), request);
        return Response.success();
    }

    @DeleteMapping("/{categoryId}")
    private Response<DeleteCategoryResponse> deleteCategory(Principal principal, @PathVariable("categoryId") Long categoryId) {
        DeleteCategoryResponse response = categoryService.deactivateCategory(principal.getName(), categoryId);
        return Response.success(response);
    }
}
