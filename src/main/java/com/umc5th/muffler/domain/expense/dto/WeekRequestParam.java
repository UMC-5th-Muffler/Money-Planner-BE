package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.global.validation.ValidWeekPeriod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidWeekPeriod
@Getter
@Setter
@NoArgsConstructor
public class WeekRequestParam {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
