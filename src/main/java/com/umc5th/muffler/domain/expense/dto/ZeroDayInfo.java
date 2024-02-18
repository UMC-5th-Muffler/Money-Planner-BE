package com.umc5th.muffler.domain.expense.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ZeroDayInfo {
    private LocalDate date;
    private boolean zeroDay;
}
