package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmControlDTO {
    private Long budget;
    private Long actualTotal;
}
