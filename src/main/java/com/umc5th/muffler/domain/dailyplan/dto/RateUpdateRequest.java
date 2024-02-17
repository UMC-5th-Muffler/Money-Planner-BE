package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.entity.constant.Rate;
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
public class RateUpdateRequest {

    @NotNull
    @ValidEnum(enumClass = Rate.class)
    private String rate;
    private String rateMemo;
}
