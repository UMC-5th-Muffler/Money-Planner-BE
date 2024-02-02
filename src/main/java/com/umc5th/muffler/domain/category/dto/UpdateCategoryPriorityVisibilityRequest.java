package com.umc5th.muffler.domain.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryPriorityVisibilityRequest {
    private List<CategoryPriorityVisibilityDTO> categories;
}
