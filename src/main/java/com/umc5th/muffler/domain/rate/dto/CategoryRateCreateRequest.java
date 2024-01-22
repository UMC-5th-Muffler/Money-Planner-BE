package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRateCreateRequest {

    @NotNull
    private Long categoryGoalId;

    @NotNull
    @ValidEnum(enumClass = Level.class)
    private String level;
}
