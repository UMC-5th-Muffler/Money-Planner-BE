package com.umc5th.muffler.domain.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateCategoryRequest {
    @NotNull
    private String memberId;
    @NotNull
    private Long categoryId;
    @NotBlank
    private String name;
    @NotBlank
    private String icon;
    @NotNull
    private Boolean isVisible;
    @NotNull
    private Long priority;
}
