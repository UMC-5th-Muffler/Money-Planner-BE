package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;

public class RateFixture {

    public static final Rate RATE_ONE = Rate.builder()
            .id(1L)
            .totalLevel(Level.HIGH)
            .memo("memo")
            .build();

    public static final Rate RATE_TWO = Rate.builder()
            .id(1L)
            .totalLevel(Level.MEDIUM)
            .memo("changeMemo")
            .build();
}
