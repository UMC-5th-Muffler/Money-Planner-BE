package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateCreateRequest {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    @ValidEnum(enumClass = Level.class)
    private String totalLevel;

    private String memo;
}
