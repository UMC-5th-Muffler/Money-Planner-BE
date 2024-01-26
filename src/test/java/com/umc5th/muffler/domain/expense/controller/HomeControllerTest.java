package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.expense.service.HomeService;
import com.umc5th.muffler.fixture.WholeCalendarResponseFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Test
    @WithMockUser
    void 날짜_홈화면_조회() throws Exception {

        WholeCalendarResponse mockResponse = WholeCalendarResponseFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));

        when(homeService.getWholeCalendarInfos("user")).thenReturn(mockResponse);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.goalId", is(mockResponse.getGoalId().intValue())))
                .andExpect(jsonPath("$.result.goalTitle", is(mockResponse.getGoalTitle())))
                .andExpect(jsonPath("$.result.goalBudget", is(mockResponse.getGoalBudget().intValue())))
                .andExpect(jsonPath("$.result.goalStartDate", is(mockResponse.getGoalStartDate().toString())))
                .andExpect(jsonPath("$.result.goalEndDate", is(mockResponse.getGoalEndDate().toString())))
                .andExpect(jsonPath("$.result.totalCost", is(mockResponse.getTotalCost().intValue())))
                .andExpect(jsonPath("$.result.dailyList", hasSize(2)))
                .andExpect(jsonPath("$.result.categoryCalendarInfo", hasSize(2)))
                .andExpect(jsonPath("$.result.otherGoalsInfo").doesNotExist());
    }

    @Test
    @WithMockUser
    void 목표_홈화면_조회() throws Exception {

        WholeCalendarResponse mockResponse = WholeCalendarResponseFixture.create(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2));

        when(homeService.getGoalCalendarInfos("user", 1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/home/{goalId}", 1))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.goalId", is(mockResponse.getGoalId().intValue())))
                .andExpect(jsonPath("$.result.goalTitle", is(mockResponse.getGoalTitle())))
                .andExpect(jsonPath("$.result.goalBudget", is(mockResponse.getGoalBudget().intValue())))
                .andExpect(jsonPath("$.result.goalStartDate", is(mockResponse.getGoalStartDate().toString())))
                .andExpect(jsonPath("$.result.goalEndDate", is(mockResponse.getGoalEndDate().toString())))
                .andExpect(jsonPath("$.result.totalCost", is(mockResponse.getTotalCost().intValue())))
                .andExpect(jsonPath("$.result.dailyList", hasSize(2)))
                .andExpect(jsonPath("$.result.categoryCalendarInfo", hasSize(2)))
                .andExpect(jsonPath("$.result.otherGoalsInfo").doesNotExist());
    }

    @Test
    @WithMockUser
    void 달_전환_홈화면_조회() throws Exception {

        WholeCalendarResponse mockResponse = new WholeCalendarResponse();

        when(homeService.getGoalTurnPage("user", 1L, 2024, 2)).thenReturn(mockResponse);

        mockMvc.perform(get("/home/{goalId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.goalId").doesNotExist());
    }
}
