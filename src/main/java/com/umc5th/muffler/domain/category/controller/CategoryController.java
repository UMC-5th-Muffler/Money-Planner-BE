package com.umc5th.muffler.domain.category.controller;

import com.umc5th.muffler.domain.category.dto.GetCategoryListResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.DeleteCategoryResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryNameIconRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryPriorityVisibilityRequest;
import com.umc5th.muffler.domain.category.service.CategoryService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.swagger.ErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/category")
@Validated
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public Response<NewCategoryResponse> createCategory(Principal principal, @RequestBody @Valid NewCategoryRequest request) {
        NewCategoryResponse newCategory = categoryService.createNewCategory(principal.getName(), request);
        return Response.success(newCategory);
    }

    @PatchMapping("/single")
    @Operation(summary = "Category icon/name update",
            description = "특정 카테고리의 아이콘이나 이름을 업데이트 한다. 가지고 있는 카테고리 중 중복되는 이름은 허용되지 않고, "
                    + "기본으로 가지고 있는 카테고리의 아이콘은 변경할 수 없다. 카테고리 아이콘/이름 수정 화면에서 저장버튼 누를 시 호출")
    @ErrorResponses(value = {
            ErrorCode.MEMBER_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.ACCESS_TO_OTHER_USER_CATEGORY,
            ErrorCode.CANNOT_UPDATE_ETC_CATEGORY_NAME, ErrorCode.DUPLICATED_CATEGORY_NAME, ErrorCode.CANNOT_UPDATE_DEFAULT_ICON
    })
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
