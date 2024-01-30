package com.umc5th.muffler.domain.goal.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.goal.dto.*;
import com.umc5th.muffler.domain.goal.service.GoalCreateService;
import com.umc5th.muffler.domain.goal.service.GoalService;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.fixture.GoalCreateRequestFixture;
import com.umc5th.muffler.fixture.GoalFixture;

import java.time.LocalDate;
import java.util.List;

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
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoalService goalService;
    @MockBean
    private GoalCreateService goalCreateService;

    @Test
    @WithMockUser
    void 이전_목표기간들을_조회한다() throws Exception {
        Goal goal = GoalFixture.create();
        when(goalService.getGoals(any())).thenReturn(List.of(goal));

        String expectedStartDate = goal.getStartDate().toString();
        String expectedEndDate = goal.getEndDate().toString();

        mockMvc.perform(get("/goal/previous"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.terms", hasSize(1)))
                .andExpect(jsonPath("$.result.terms[0].startDate", is(expectedStartDate)))
                .andExpect(jsonPath("$.result.terms[0].endDate", is(expectedEndDate)));
    }

    @Test
    @WithMockUser
    void 목표를_생성한다() throws Exception {
        GoalCreateRequest request = GoalCreateRequestFixture.create();

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))).andDo(print())
                .andExpect(status().isOk());

        verify(goalCreateService).create(any(GoalCreateRequest.class), any());
    }

    @Test
    @WithMockUser
    void 목표를_삭제한다() throws Exception {
        mockMvc.perform(delete("/goal/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 목표_탭_진행중목표_조회() throws Exception {
        GoalInfo mockResponse = GoalInfo.builder()
                .title("progress").icon("icon")
                .totalBudget(10000L).totalCost(1000L)
                .endDate(LocalDate.of(2024, 3, 1))
                .build();

        when(goalService.getGoalNow("user")).thenReturn(mockResponse);

        mockMvc.perform(get("/goal/now"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title", is(mockResponse.getTitle())))
                .andExpect(jsonPath("$.result.icon", is(mockResponse.getIcon())))
                .andExpect(jsonPath("$.result.totalBudget", is(mockResponse.getTotalBudget().intValue())))
                .andExpect(jsonPath("$.result.totalCost", is(mockResponse.getTotalCost().intValue())))
                .andExpect(jsonPath("$.result.endDate", is(mockResponse.getEndDate().toString())));
    }

    @Test
    @WithMockUser
    void 목표_탭_목표전체조회() throws Exception {

        GoalInfo past = GoalInfo.builder()
                .title("endedGoal").icon("icon")
                .totalBudget(10000L).totalCost(1000L)
                .build();

        GoalInfo future = GoalInfo.builder()
                .title("futureGoal").icon("icon")
                .totalBudget(10000L).totalCost(1000L)
                .build();

        GoalPreviewResponse mockResponse = GoalPreviewResponse.builder()
                .futureGoal(List.of(future))
                .endedGoal(List.of(past))
                .build();

        when(goalService.getGoalPreview("user")).thenReturn(mockResponse);

        mockMvc.perform(get("/goal/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.futureGoal", hasSize(1)))
                .andExpect(jsonPath("$.result.futureGoal[0].title", is(mockResponse.getFutureGoal().get(0).getTitle())))
                .andExpect(jsonPath("$.result.endedGoal", hasSize(1)))
                .andExpect(jsonPath("$.result.endedGoal[0].title", is(mockResponse.getEndedGoal().get(0).getTitle())));
    }

    @Test
    @WithMockUser
    void 목표_리스트_조회() throws Exception {

        GoalListInfo info1 = GoalListInfo.builder().title("title1").icon("icon").build();
        GoalListInfo info2 = GoalListInfo.builder().title("title2").icon("icon").build();
        GoalListResponse mockResponse = GoalListResponse.builder().goalList(List.of(info1, info2)).build();

        when(goalService.getGoalList("user")).thenReturn(mockResponse);

        mockMvc.perform(get("/goal/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.goalList", hasSize(2)))
                .andExpect(jsonPath("$.result.goalList[0].title", is(mockResponse.getGoalList().get(0).getTitle())));
    }
}
