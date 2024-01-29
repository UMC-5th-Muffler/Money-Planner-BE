package com.umc5th.muffler.domain.dailyplan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.fixture.DailyPlanFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import com.umc5th.muffler.fixture.RateUpdateRequestFixture;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class RateServiceTest {

    @Autowired
    private RateService rateService;

    @MockBean
    private DailyPlanRepository dailyPlanRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    public void 평가항목조회_기존_평가가_없는_경우(){
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        LocalDate date = LocalDate.of(2024, 1, 2);
        DailyPlan dailyPlan = DailyPlanFixture.DAILY_PLAN_NO_RATE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(dailyPlan));

        RateInfoResponse response = rateService.getRateInfo(memberId, date);

        assertNotNull(response);
        assertEquals(dailyPlan.getBudget(), response.getDailyPlanBudget());
        assertEquals(dailyPlan.getTotalCost(), response.getDailyTotalCost());

        verify(dailyPlanRepository).findByDate(date);
    }

    @Test
    public void 평가항목조회_기존_평가가_있는_경우(){
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        LocalDate date = LocalDate.of(2024, 1, 1);
        DailyPlan dailyPlan = DailyPlanFixture.DAILY_PLAN_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(dailyPlan));

        RateInfoResponse response = rateService.getRateInfo(memberId, date);

        assertNotNull(response);
        assertEquals(dailyPlan.getBudget(), response.getDailyPlanBudget());
        assertEquals(dailyPlan.getTotalCost(), response.getDailyTotalCost());
        assertEquals(dailyPlan.getRate(), response.getRate());
        assertEquals(dailyPlan.getRateMemo(), response.getMemo());

        verify(dailyPlanRepository).findByDate(date);
    }

    @Test
    public void 평가항목조회_오늘날짜에_일일계획이_없는_경우(){
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        LocalDate date = LocalDate.now();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        assertThrows(DailyPlanException.class, () -> rateService.getRateInfo(memberId, date));
    }

    @Test
    void 평가_등록_수정_성공() {
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        LocalDate date = LocalDate.of(2024, 1, 1);
        DailyPlan originalDailyPlan = DailyPlanFixture.DAILY_PLAN_ONE;
        RateUpdateRequest request = RateUpdateRequestFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dailyPlanRepository.findByDate(date)).thenReturn(Optional.of(originalDailyPlan));

        // 변경 전 상태 확인
        Rate originalRate = originalDailyPlan.getRate();
        String originalMemo = originalDailyPlan.getRateMemo();

        rateService.updateRate(memberId, date, request);

        // 변경 후 상태 확인
        DailyPlan updatedDailyPlan = dailyPlanRepository.findByDate(date).get();

        assertNotEquals(originalRate, updatedDailyPlan.getRate());
        assertNotEquals(originalMemo, updatedDailyPlan.getRateMemo());
    }


}