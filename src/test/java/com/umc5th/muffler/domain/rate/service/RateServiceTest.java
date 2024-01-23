package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.rate.repository.RateRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.fixture.*;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.RateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class RateServiceTest {

    @Autowired
    private RateService rateService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private GoalRepository goalRepository;

    @MockBean
    private RateRepository rateRepository;

    @Test
    public void 평가항목조회_기존_평가가_없는_경우(){

        LocalDate date = LocalDate.of(2024, 1, 2);
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.createWithoutCategoryGoals();
        Long dailyTotalCost = DailyPlanFixture.DAILY_PLAN_TWO.getTotalCost();
        Long dailyPlanBudget = DailyPlanFixture.DAILY_PLAN_TWO.getBudget();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));

        RateCriteriaResponse response = rateService.getRateCriteria(date, memberId);

        assertNotNull(response);
        assertEquals(dailyPlanBudget, response.getDailyPlanBudget());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(null, response.getRateId());


        verify(goalRepository).findByDateBetween(date, memberId);
    }

    @Test
    public void 평가항목조회_기존_평가가_있는_경우(){

        LocalDate date = LocalDate.of(2024, 1, 1);
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        Long dailyTotalCost = DailyPlanFixture.DAILY_PLAN_ONE.getTotalCost();
        Long dailyPlanBudget = DailyPlanFixture.DAILY_PLAN_ONE.getBudget();
        Rate rate = RateFixture.RATE_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));

        RateCriteriaResponse response = rateService.getRateCriteria(date, memberId);

        assertNotNull(response);
        assertEquals(dailyPlanBudget, response.getDailyPlanBudget());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(rate.getId(), response.getRateId());
        assertEquals(rate.getTotalLevel(), response.getTotalLevel());

        verify(goalRepository).findByDateBetween(date, memberId);
    }

    @Test
    public void 평가항목조회_오늘날짜에_목표가_없는_경우(){

        LocalDate date = LocalDate.now();
        String memberId = "1";
        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenThrow(new GoalException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));

        assertThrows(GoalException.class, () -> rateService.getRateCriteria(date, memberId));
    }

    @Test
    public void 평가항목조회_오늘날짜에_일일계획이_없는_경우(){
        LocalDate date = LocalDate.now();
        String memberId = "1";
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));

        assertThrows(RateException.class, () -> rateService.getRateCriteria(date, memberId));
    }

    @Test
    public void 평가_등록_성공(){
        LocalDate date = LocalDate.of(2024, 1, 1);
        String memberId = "1";
        RateCreateRequest request = RateCreateRequestFixture.create();

        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.createWithoutRate();
        Rate mockRate = mock(Rate.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));
        when(rateRepository.save(any())).thenReturn(mockRate);

        rateService.createRate(request, memberId);

        verify(rateRepository).save(any(Rate.class));
    }

    @Test
    public void 평가_등록_목표가_없는_경우() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String memberId = "1";
        RateCreateRequest request = RateCreateRequestFixture.create();
        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.empty());

        assertThrows(GoalException.class, () -> rateService.createRate(request, memberId));

        verifyNoInteractions(rateRepository);
    }

    @Test
    public void 평가_등록_일일계획이_없는_경우() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String memberId = "1";
        RateCreateRequest request = RateCreateRequestFixture.create();
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.createWithoutDailyPlans();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));

        assertThrows(RateException.class, () -> rateService.createRate(request, memberId));

        verifyNoInteractions(rateRepository);
    }

    @Test
    void 평가_수정_성공() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String memberId = "1";
        Long rateId = 1L;
        RateUpdateRequest request = RateUpdateRequestFixture.create();

        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        Rate originalRate = RateFixture.RATE_ONE; // 초기 Rate 상태

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetween(date, memberId)).thenReturn(Optional.of(mockGoal));
        when(rateRepository.findById(rateId)).thenReturn(Optional.of(originalRate));

        // 변경 전 상태 확인
        Level originalLevel = originalRate.getTotalLevel();
        String originalMemo = originalRate.getMemo();

        rateService.updateRate(request);

        // 변경 후 상태 확인
        when(rateRepository.findById(rateId)).thenReturn(Optional.of(originalRate));
        Rate updatedRate = rateRepository.findById(rateId).get();

        assertNotEquals(originalLevel, updatedRate.getTotalLevel());
        assertNotEquals(originalMemo, updatedRate.getMemo());
    }


    @Test
    void 평가_수정_기존평가가_없는_경우() {
        String memberId = "1";
        RateUpdateRequest request = RateUpdateRequestFixture.create();

        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(rateRepository.findById(request.getRateId())).thenReturn(Optional.empty());

        assertThrows(RateException.class, () -> rateService.updateRate(request));
    }


}