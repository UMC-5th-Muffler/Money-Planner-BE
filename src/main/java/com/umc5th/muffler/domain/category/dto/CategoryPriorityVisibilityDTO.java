package com.umc5th.muffler.domain.category.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryPriorityVisibilityDTO {
    @NotNull
    @Positive
    private Long categoryId;
    @NotNull
    private Boolean isVisible;
    @NotNull
    @Positive
    private Long priority;
}
