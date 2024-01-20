package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.rate.dto.CategoryRateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;

import java.time.LocalDate;
import java.util.List;

public class RateCreateRequestFixture {

    public static RateCreateRequest create(){
        return RateCreateRequest.builder()
                .date(LocalDate.of(2024, 1, 1))
                .totalLevel("HIGH")
                .memo("memo")
                .categoryRateList(List.of(new CategoryRateCreateRequest(1L, "LOW")))
                .build();
    }
}
