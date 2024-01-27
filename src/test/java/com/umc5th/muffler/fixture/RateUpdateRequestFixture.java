package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;

import java.time.LocalDate;

public class RateUpdateRequestFixture {

    public static RateUpdateRequest create(){
        return RateUpdateRequest.builder()
                .rate("MEDIUM")
                .memo("changeMemo")
                .build();
    }
}
