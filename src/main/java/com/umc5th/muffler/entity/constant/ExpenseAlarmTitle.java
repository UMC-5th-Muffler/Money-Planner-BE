package com.umc5th.muffler.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpenseAlarmTitle {
    DAILY("하루"),
    CATEGORY("카테고리"),
    TOTAL("전체");

    private final String title;

    @Override
    public String toString() {
        return this.title;
    }
}
