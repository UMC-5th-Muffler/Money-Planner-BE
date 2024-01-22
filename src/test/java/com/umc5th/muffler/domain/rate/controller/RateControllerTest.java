package com.umc5th.muffler.domain.rate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.rate.service.RateService;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.fixture.RateCreateRequestFixture;
import com.umc5th.muffler.fixture.RateUpdateRequestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        RateCriteriaResponse mockResponse = RateCriteriaResponse.builder()
                .isZeroDay(false)
                .dailyTotalCost(1000L)
                .dailyPlanBudget(5000L)
                .totalLevel(Level.HIGH)
                .rateId(1L)
                .memo("memo")
                .build();

        when(rateService.getRateCriteria(date, "user")).thenReturn(mockResponse);

        mockMvc.perform(get("/rate")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalLevel", is(mockResponse.getTotalLevel().toString())));
    }

    @Test
    @WithMockUser
    void 평가_등록() throws Exception {
        RateCreateRequest mockRequest = RateCreateRequestFixture.create();
        mockMvc.perform(post("/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk());
        verify(rateService).createRate(any(RateCreateRequest.class), any());
    }

    @Test
    @WithMockUser
    void 평가_수정() throws Exception {
        RateUpdateRequest mockRequest = RateUpdateRequestFixture.create();

        mockMvc.perform(patch("/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk());
        verify(rateService).updateRate(any(RateUpdateRequest.class));
    }
}