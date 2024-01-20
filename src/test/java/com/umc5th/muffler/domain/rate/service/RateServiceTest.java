package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.repository.RateRepository;
import com.umc5th.muffler.entity.CategoryRate;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Rate;
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
        Long memberId = 1L;
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        Long dailyTotalCost = DailyPlanFixture.DAILY_PLAN_TWO.getTotalCost();
        Long dailyPlanBudget = DailyPlanFixture.DAILY_PLAN_TWO.getBudget();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));

        RateCriteriaResponse response = rateService.getEvalCategoryList(date);

        assertNotNull(response);
        assertEquals(dailyPlanBudget, response.getDailyPlanBudget());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(1, response.getCategoryList().size());
        assertEquals(null, response.getRateId());


        verify(goalRepository).findByDateBetweenJoin(date, memberId);
    }

    @Test
    public void 평가항목조회_기존_평가가_있는_경우(){

        LocalDate date = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        Long dailyTotalCost = DailyPlanFixture.DAILY_PLAN_ONE.getTotalCost();
        Long dailyPlanBudget = DailyPlanFixture.DAILY_PLAN_ONE.getBudget();
        Rate rate = RateFixture.RATE_ONE;
        CategoryRate categoryRate = CategoryRateFixture.CATEGORY_RATE_ONE;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));

        RateCriteriaResponse response = rateService.getEvalCategoryList(date);

        assertNotNull(response);
        assertEquals(dailyPlanBudget, response.getDailyPlanBudget());
        assertEquals(dailyTotalCost, response.getDailyTotalCost());
        assertEquals(1, response.getCategoryList().size());
        assertEquals(rate.getId(), response.getRateId());
        assertEquals(rate.getTotalLevel(), response.getTotalLevel());
        assertEquals(categoryRate.getLevel(), response.getCategoryList().get(0).getLevel());
        assertEquals(categoryRate.getId(), response.getCategoryList().get(0).getCategoryRateId());

        verify(goalRepository).findByDateBetweenJoin(date, memberId);
    }

    @Test
    public void 평가항목조회_오늘날짜에_목표가_없는_경우(){

        LocalDate date = LocalDate.now();
        Long memberId = 1L;
        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenThrow(new GoalException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));

        assertThrows(GoalException.class, () -> rateService.getEvalCategoryList(date));
    }

    @Test
    public void 평가항목조회_오늘날짜에_일일계획이_없는_경우(){
        LocalDate date = LocalDate.now();
        Long memberId = 1L;
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));

        assertThrows(RateException.class, () -> rateService.getEvalCategoryList(date));
    }

    @Test
    public void 평가_등록_성공(){
        LocalDate date = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        RateCreateRequest request = RateCreateRequestFixture.create();

        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.create();
        Rate mockRate = mock(Rate.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));
        when(rateRepository.save(any())).thenReturn(mockRate);

        rateService.createRate(request);

        verify(rateRepository).save(any(Rate.class));
    }

    @Test
    public void 평가_등록_목표가_없는_경우() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        RateCreateRequest request = RateCreateRequestFixture.create();
        Member mockMember = MemberFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.empty());

        assertThrows(GoalException.class, () -> rateService.createRate(request));

        verifyNoInteractions(rateRepository);
    }

    @Test
    public void 평가_등록_일일계획이_없는_경우() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        RateCreateRequest request = RateCreateRequestFixture.create();
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.createWithoutDailyPlans();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));

        assertThrows(RateException.class, () -> rateService.createRate(request));

        verifyNoInteractions(rateRepository);
    }

    @Test
    public void 평가_등록_일치하는_카테고리목표가_없는_경우() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        Long memberId = 1L;
        RateCreateRequest request = RateCreateRequestFixture.create();
        Member mockMember = MemberFixture.create();
        Goal mockGoal = GoalFixture.createWithoutCategoryGoals();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findByDateBetweenJoin(date, memberId)).thenReturn(Optional.of(mockGoal));

        assertThrows(GoalException.class, () -> rateService.createRate(request));

        verifyNoInteractions(rateRepository);
    }

}