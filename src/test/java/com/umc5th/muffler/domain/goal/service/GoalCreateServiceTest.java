package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_DAILY_PLAN;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_GOAL_DATE;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.GoalCreateRequestFixture;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class GoalCreateServiceTest {

    @Autowired
    private GoalCreateService goalCreateService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private GoalRepository goalRepository;


    @Test
    void 목표등록이_성공한경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequestFixture.create();
        Member mockMember = mock(Member.class);
        Goal mockGoal = mock(Goal.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.save(any())).thenReturn(mockGoal);

        goalCreateService.create(request, memberId);

        verify(memberRepository).findById(memberId);
        verify(goalRepository).save(any(Goal.class));
        verify(mockMember).addGoal(mockGoal);
    }

    @Test
    void 목표가_1일인기간을_입력한경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequestFixture.create(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_DATE);
    }

    @Test
    void 목표등록시_시작기간이_끝기간보다_느린경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequestFixture.create(LocalDate.of(2024, 1, 1), LocalDate.of(2023, 1, 1));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_DATE);
    }

    @Test
    void 목표기간보다_계획기간이_짧은경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .dailyBudgets(List.of(1000L))
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_GOAL_DATE);
    }

    @Test
    void 전체목표금액이_일일계획금액총합과_일치하지않는경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .dailyBudgets(List.of(1000L, 1000L))
                .totalBudget(1000L)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_DAILY_PLAN);
    }

    @Test
    void 목표등록시_요청한유저가_존재하지않는경우() {
        Long memberId = 1L;
        GoalCreateRequest request = GoalCreateRequestFixture.create();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalCreateService.create(request, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

}