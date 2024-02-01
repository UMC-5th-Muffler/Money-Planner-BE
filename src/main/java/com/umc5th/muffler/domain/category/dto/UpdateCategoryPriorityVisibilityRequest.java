package com.umc5th.muffler.domain.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateCategoryPriorityVisibilityRequest {
    private List<CategoryPriorityVisibilityDTO> categories;
}
