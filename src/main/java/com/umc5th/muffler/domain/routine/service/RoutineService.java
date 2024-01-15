package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.umc5th.muffler.global.response.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void delete(Long routineId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Routine routine = routineRepository.findById(routineId).orElseThrow(() -> new RoutineException(ROUTINE_NOT_FOUND));

        if(!Objects.equals(member.getId(), routine.getMember().getId())) {
            throw new CommonException(INVALID_PERMISSION);
        }

        routineRepository.delete(routine);
        member.removeRoutine(routine);
    }
}
