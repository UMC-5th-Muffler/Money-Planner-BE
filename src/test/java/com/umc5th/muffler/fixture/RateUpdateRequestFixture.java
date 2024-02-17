package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;


public class RateUpdateRequestFixture {

    public static RateUpdateRequest create(){
        return RateUpdateRequest.builder()
                .rate("MEDIUM")
                .rateMemo("changeMemo")
                .build();
    }
}
