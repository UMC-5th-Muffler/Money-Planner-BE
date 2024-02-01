package com.umc5th.muffler.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDetailDto {

    private Long id;
    private String name;
}
