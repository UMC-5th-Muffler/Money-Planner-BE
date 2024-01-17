package com.umc5th.muffler.domain.home.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryInfoDto {

    private Long id;
    private String name;
}
