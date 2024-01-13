package com.umc5th.muffler.domain.category.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewCategoryRequest {
    @NotBlank
    private String categoryName;
}
