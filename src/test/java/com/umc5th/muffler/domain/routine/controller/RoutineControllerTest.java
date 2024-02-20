package com.umc5th.muffler.domain.routine.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.umc5th.muffler.config.TestSecurityConfig;
import com.umc5th.muffler.domain.routine.dto.RoutineAll;
import com.umc5th.muffler.domain.routine.dto.RoutineDetail;
import com.umc5th.muffler.domain.routine.dto.RoutineResponse;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class RoutineControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoutineService routineService;

    @Test
    @WithMockUser
    void 루틴_전체조회() throws Exception {

        RoutineAll routineAll = RoutineAll.builder()
                .routineId(1L)
                .routineTitle("title")
                .routineCost(1000L)
                .categoryIcon("icon")
                .monthlyRepeatDay("1")
                .build();

        RoutineResponse mockResponse = RoutineResponse.builder()
                        .routineList(List.of(routineAll))
                        .hasNext(false)
                        .build();

        when(routineService.getAllRoutines(any(Pageable.class), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/routine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.routineList", hasSize(1)))
                .andExpect(jsonPath("$.result.routineList[0].routineId", is(mockResponse.getRoutineList().get(0).getRoutineId().intValue())))
                .andExpect(jsonPath("$.result.routineList[0].routineTitle", is(mockResponse.getRoutineList().get(0).getRoutineTitle())))
                .andExpect(jsonPath("$.result.routineList[0].categoryIcon", is(mockResponse.getRoutineList().get(0).getCategoryIcon())))
                .andExpect(jsonPath("$.result.routineList[0].monthlyRepeatDay", is(mockResponse.getRoutineList().get(0).getMonthlyRepeatDay())));
    }

    @Test
    @WithMockUser
    void 루틴_상세조회() throws Exception {

        Long routineId = 1L;
        RoutineDetail routineDetail = RoutineDetail.builder()
                .routineMemo("memo")
                .categoryName("식비")
                .build();

        when(routineService.getRoutine(any(), eq(routineId))).thenReturn(routineDetail);

        mockMvc.perform(get("/api/routine/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.routineMemo", is(routineDetail.getRoutineMemo())))
                .andExpect(jsonPath("$.result.categoryName", is(routineDetail.getCategoryName())));
    }

    @Test
    @WithMockUser
    void 루틴_삭제() throws Exception {
        mockMvc.perform(delete("/api/routine/1"))
                .andExpect(status().isOk());
    }
}
