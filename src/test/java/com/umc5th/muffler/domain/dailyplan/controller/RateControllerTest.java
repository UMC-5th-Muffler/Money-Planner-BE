package com.umc5th.muffler.domain.dailyplan.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.service.RateService;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.fixture.RateUpdateRequestFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RateService rateService;

    @Test
    @WithMockUser
    void 평가_화면_조회() throws Exception {
        LocalDate date = LocalDate.now();
        RateInfoResponse mockResponse = RateInfoResponse.builder()
                .isZeroDay(false)
                .dailyTotalCost(1000L)
                .dailyPlanBudget(5000L)
                .rate(Rate.HIGH)
                .rateMemo("memo")
                .build();

        when(rateService.getRateInfo(any(), eq(date))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/rate")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.rate", is(mockResponse.getRate().toString())));
    }

    @Test
    @WithMockUser
    void 평가_등록_수정() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        RateUpdateRequest mockRequest = RateUpdateRequestFixture.create();

        mockMvc.perform(patch("/api/rate/{date}", date.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk());
        verify(rateService).updateRate(any(), eq(date), any(RateUpdateRequest.class));
    }
}