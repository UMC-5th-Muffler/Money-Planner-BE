package com.umc5th.muffler.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonthlyRepeatType {
    FIRST_DAY_OF_MONTH("매월 첫째 날"),
    LAST_DAY_OF_MONTH("매월 마지막 날"),
    SPECIFIC_DAY_OF_MONTH("");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }
}
