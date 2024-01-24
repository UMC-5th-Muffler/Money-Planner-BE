package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;

import java.util.ArrayList;
import java.util.List;

public class RateFixture {

    public static final Rate RATE_ONE = Rate.builder()
            .id(1L)
            .totalLevel(Level.HIGH)
            .memo("memo")
            .categoryRates(new ArrayList<>(List.of(CategoryRateFixture.CATEGORY_RATE_ONE)))
            .build();

    public static final Rate RATE_TWO = Rate.builder()
            .id(1L)
            .totalLevel(Level.MEDIUM)
            .memo("changeMemo")
            .categoryRates(List.of(CategoryRateFixture.CATEGORY_RATE_ONE))
            .build();
}
