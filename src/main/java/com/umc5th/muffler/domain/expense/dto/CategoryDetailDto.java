package com.umc5th.muffler.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDetailDto {

    private Long id; // 카테고리 id
    private String name; // 카테고리 이름
}
