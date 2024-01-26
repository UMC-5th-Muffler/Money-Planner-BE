package com.umc5th.muffler.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DefaultCategoryDTO {
    private String name;
    private String icon;
    private Long priority;
    private String status;
    private Boolean isVisible;
    private String type;
    private String memberId;
}
