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
import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.service.GoalCreateService;
import com.umc5th.muffler.domain.goal.service.GoalService;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.fixture.GoalCreateRequestFixture;
import com.umc5th.muffler.fixture.GoalFixture;
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
}
