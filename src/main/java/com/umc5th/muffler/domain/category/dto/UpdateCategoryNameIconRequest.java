package com.umc5th.muffler.domain.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCategoryNameIconRequest {
    @NotNull
    @Positive
    private Long categoryId;
    @NotBlank
    private String name;
    @NotBlank
    private String icon;
}
