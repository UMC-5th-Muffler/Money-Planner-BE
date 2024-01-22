package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.rate.dto.RateUpdateRequest;

import java.time.LocalDate;

public class RateUpdateRequestFixture {

    public static RateUpdateRequest create(){
        return RateUpdateRequest.builder()
                .date(LocalDate.of(2024, 1, 1))
                .rateId(1L)
                .totalLevel("MEDIUM")
                .memo("changeMemo")
                .build();
    }
}
