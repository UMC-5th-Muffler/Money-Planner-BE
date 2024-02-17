package com.umc5th.muffler.domain.dailyplan.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.dailyplan.service.HomeService;
import com.umc5th.muffler.fixture.HomeFixture;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HomeService homeService;

    @Test
    @WithMockUser
    void 오늘_달력조회() throws Exception {
        when(homeService.getNowCalendar(any()))
                .thenReturn(HomeFixture.createWholeCalendar());

        mockMvc.perform(get("/api/home/now"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    @WithMockUser(username = "1")
    void 목표없는_달력조회() throws Exception {
        YearMonth yearMonth = YearMonth.of(2024, 1);
        String memberId = "1";

        when(homeService.getBasicCalendar(memberId, yearMonth))
                .thenReturn(HomeFixture.createWholeCalendar());

        mockMvc.perform(get("/api/home/basic?yearMonth={yearMonth}", yearMonth))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.result").exists());

        verify(homeService).getBasicCalendar(memberId, yearMonth);
    }

    @Test
    @WithMockUser
    void 초기골_달력조회() throws Exception {
        Long goalId = 1L;

        when(homeService.getDefaultGoalCalendar(anyString(), eq(goalId)))
                .thenReturn(HomeFixture.createWholeCalendar());

        mockMvc.perform(get("/api/home/goal/{goalId}", goalId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    @WithMockUser
    void 날짜골_달력조회() throws Exception {
        Long goalId = 1L;
        YearMonth yearMonth = YearMonth.of(2024, 1);

        when(homeService.getDateGoalCalendar(anyString(), eq(goalId), eq(yearMonth)))
                .thenReturn(HomeFixture.createWholeCalendar());

        mockMvc.perform(get("/api/home/goal/{goalId}/{yearMonth}", goalId, yearMonth))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    @WithMockUser
    void 카테고리_달력조회() throws Exception {
        Long goalId = 1L;
        YearMonth yearMonth = YearMonth.of(2024, 1);
        Long categoryId = 2L;

        when(homeService.getCategoryCalendar(anyString(), eq(goalId), eq(yearMonth), eq(categoryId)))
                .thenReturn(HomeFixture.createWholeCalendar());

        mockMvc.perform(get("/api/home/goal/{goalId}/category/{categoryId}?yearMonth={yearMonth}",
                        goalId, categoryId, yearMonth))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.result").exists());
    }
}
