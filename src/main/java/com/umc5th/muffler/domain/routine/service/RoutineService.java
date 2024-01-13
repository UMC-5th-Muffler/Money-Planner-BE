package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.repository.WeeklyRoutineRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final MemberRepository memberRepository;
    private final WeeklyRoutineRepository weeklyRoutineRepository;

    public WeeklyRoutineExpense addWeeklyRoutine(WeeklyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        WeeklyRoutineExpense newWeeklyRoutineExpense = RoutineConverter.toWeeklyRoutine(request, member);

        return weeklyRoutineRepository.save(newWeeklyRoutineExpense);
    }


}
