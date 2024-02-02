package com.umc5th.muffler.domain.category.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class OutlineCategoryDTO {
    private Long categoryId;
    private String name;
}
