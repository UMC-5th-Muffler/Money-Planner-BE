package com.umc5th.muffler.domain.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewCategoryRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String icon;
    @NotNull
    private Long priority;
}
