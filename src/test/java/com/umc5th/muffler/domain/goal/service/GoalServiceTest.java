package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_PERMISSION;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.umc5th.muffler.domain.goal.dto.GoalInfo;
import com.umc5th.muffler.domain.goal.dto.GoalPreviewResponse;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

@SpringBootTest
class GoalServiceTest {

    @Autowired
    private GoalService goalService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private GoalRepository goalRepository;

    @Test
    void 전체_목표조회가_성공한경우() {
        String memberId = "1";
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatCode(() -> goalService.getGoals(memberId)).doesNotThrowAnyException();

        verify(memberRepository).findById(memberId);
    }

    @Test
    void 전체목표조회시_요청한유저가_존재하지않는경우() {
        String memberId = "1";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoals(memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표삭제에_성공할경우() {
        String memberId = "1";
        Long goalId = 1L;

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        Goal mockGoal = mock(Goal.class);
        when(mockGoal.getMember()).thenReturn(mockMember);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));

        goalService.delete(goalId, memberId);

        verify(goalRepository).delete(mockGoal);
        verify(mockMember).removeGoal(mockGoal);
    }

    @Test
    void 목표삭제시_요청한유저가_존재하지않는경우() {
        String memberId = "1";
        Long goalId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }

    @Test
    void 목표삭제시_해당목표가_존재하지않는경우() {
        String memberId = "1";
        Long goalId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_NOT_FOUND);
    }

    @Test
    void 목표삭제시_등록한유저가_아닌경우() {
        String memberId = "1";
        Long goalId = 1L;

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        Goal mockGoal = mock(Goal.class);
        when(mockGoal.getMember()).thenReturn(mock(Member.class));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(mockGoal));

        assertThatThrownBy(() -> goalService.delete(goalId, memberId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("errorCode", INVALID_PERMISSION);
    }

    @Test
    void 목표_탭_진행중_조회_성공() {
        String memberId = "1";
        LocalDate today = LocalDate.now();
        Goal mockGoal = GoalFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));

        when(goalRepository.findByDateBetweenAndDailyPlans(today, memberId)).thenReturn(Optional.of(mockGoal));

        GoalInfo response = goalService.getGoalNow(memberId);

        assertNotNull(response);
        assertEquals(mockGoal.getId(), response.getGoalId());

        verify(goalRepository).findByDateBetweenAndDailyPlans(today, memberId);
    }

    @Test
    void 목표_탭_전체조회_성공() {
        String memberId = "1";
        LocalDate today = LocalDate.now();
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.DESC,"startDate").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(0, pageSize, sort);

        Goal mockEndedGoal = GoalFixture.create();
        Goal mockFutureGoal = GoalFixture.create(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2));
        List<Goal> goalList = List.of(mockFutureGoal, mockEndedGoal);
        Slice<Goal> goalSlice = new SliceImpl<>(goalList, pageable, false);

        when(goalRepository.findByMemberIdAndDailyPlans(memberId, pageable, today, null)).thenReturn(goalSlice);

        GoalPreviewResponse response = goalService.getGoalPreview(memberId, pageable, null);

        assertNotNull(response);
        assertEquals(mockEndedGoal.getId(), response.getEndedGoal().get(0).getGoalId());
        assertEquals(mockFutureGoal.getId(), response.getFutureGoal().get(0).getGoalId());
        assertEquals(goalSlice.hasNext(), response.getHasNext());

        verify(goalRepository).findByMemberIdAndDailyPlans(memberId, pageable, today, null);
    }

    @Test
    void 목표_리스트_조회_성공() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));

        assertThatCode(() -> goalService.getGoalList(memberId)).doesNotThrowAnyException();

        verify(memberRepository).findById(memberId);
    }

    @Test
    void 목표_리스트_사용자_존재X() {
        String memberId = "1";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalList(memberId))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBER_NOT_FOUND);
    }
}
