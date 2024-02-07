package com.umc5th.muffler.domain.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCategoryListResponse {
    private List<CategoryDTO> categories;
}
