package com.umc5th.muffler.domain.category.controller;

import com.umc5th.muffler.domain.category.dto.GetCategoryListResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.DeleteCategoryResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryNameIconRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryPriorityVisibilityRequest;
import com.umc5th.muffler.domain.category.service.CategoryService;
import com.umc5th.muffler.global.response.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PatchMapping("/single")
    public Response<Void> updateCategoryNameOrIcon(Principal principal, @RequestBody @Valid UpdateCategoryNameIconRequest request) {
        categoryService.updateNameOrIcon(principal.getName(), request);
        return Response.success();
    }
    @PatchMapping
    public Response<Void> updateCategoryPriorityVisibility(Principal principal, @RequestBody @Valid UpdateCategoryPriorityVisibilityRequest request) {
        categoryService.updateBatchPriorityOrVisibility(principal.getName(), request);
        return Response.success();
    }

    @DeleteMapping("/{categoryId}")
    public Response<DeleteCategoryResponse> deleteCategory(Principal principal, @PathVariable("categoryId") Long categoryId) {
        DeleteCategoryResponse response = categoryService.deactivateCategory(principal.getName(), categoryId);
        return Response.success(response);
    }
    @GetMapping
    public Response<GetCategoryListResponse> getCategoryList(Principal principal) {
        GetCategoryListResponse response = this.categoryService.getActiveCategories(principal.getName());
        return Response.success(response);
    }
}
