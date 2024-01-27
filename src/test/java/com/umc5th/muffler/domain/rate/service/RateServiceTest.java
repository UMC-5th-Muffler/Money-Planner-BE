package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.dailyplan.service.DailyPlanService;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.fixture.DailyPlanFixture;
import com.umc5th.muffler.fixture.RateUpdateRequestFixture;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class RateServiceTest {

    @Autowired
    private DailyPlanService dailyPlanService;

    @MockBean
    private DailyPlanRepository dailyPlanRepository;

    @Test
    public void 평가항목조회_기존_평가가_없는_경우(){
        LocalDate date = LocalDate.of(2024, 1, 2);
        DailyPlan dailyPlan = DailyPlanFixture.DAILY_PLAN_NO_RATE;

        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(dailyPlan));

        RateInfoResponse response = dailyPlanService.getRateInfo(date);

        assertNotNull(response);
        assertEquals(dailyPlan.getBudget(), response.getDailyPlanBudget());
        assertEquals(dailyPlan.getTotalCost(), response.getDailyTotalCost());

        verify(dailyPlanRepository).findByDate(date);
    }

    @Test
    public void 평가항목조회_기존_평가가_있는_경우(){
        LocalDate date = LocalDate.of(2024, 1, 1);
        DailyPlan dailyPlan = DailyPlanFixture.DAILY_PLAN_ONE;

        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(dailyPlan));

        RateInfoResponse response = dailyPlanService.getRateInfo(date);

        assertNotNull(response);
        assertEquals(dailyPlan.getBudget(), response.getDailyPlanBudget());
        assertEquals(dailyPlan.getTotalCost(), response.getDailyTotalCost());
        assertEquals(dailyPlan.getRate(), response.getRate());
        assertEquals(dailyPlan.getRateMemo(), response.getMemo());

        verify(dailyPlanRepository).findByDate(date);
    }

    @Test
    public void 평가항목조회_오늘날짜에_일일계획이_없는_경우(){
        LocalDate date = LocalDate.now();
        assertThrows(DailyPlanException.class, () -> dailyPlanService.getRateInfo(date));
    }

    @Test
    void 평가_등록_수정_성공() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        DailyPlan originalDailyPlan = DailyPlanFixture.DAILY_PLAN_ONE;
        RateUpdateRequest request = RateUpdateRequestFixture.create();

        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(originalDailyPlan));

        // 변경 전 상태 확인
        Level originalRate = originalDailyPlan.getRate();
        String originalMemo = originalDailyPlan.getRateMemo();

        dailyPlanService.updateRate(date, request);

        // 변경 후 상태 확인
        DailyPlan updatedDailyPlan = dailyPlanRepository.findByDate(date).get();

        assertNotEquals(originalRate, updatedDailyPlan.getRate());
        assertNotEquals(originalMemo, updatedDailyPlan.getRateMemo());
    }


}