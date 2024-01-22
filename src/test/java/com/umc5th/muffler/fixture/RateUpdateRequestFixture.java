package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.rate.dto.CategoryRateUpdateRequest;
import com.umc5th.muffler.domain.rate.dto.RateUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public class RateUpdateRequestFixture {

    public static RateUpdateRequest create(){
        return RateUpdateRequest.builder()
                .date(LocalDate.of(2024, 1, 1))
                .rateId(1L)
                .totalLevel("MEDIUM")
                .memo("changeMemo")
                .categoryRateList(List.of(new CategoryRateUpdateRequest(1L, 1L, "HIGH"),
                        new CategoryRateUpdateRequest(null, 2L, "LOW")))
                .build();
    }
}
