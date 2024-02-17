package com.umc5th.muffler.domain.dailyplan.service;

import com.umc5th.muffler.domain.dailyplan.dto.RateConverter;
import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Rate;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {

    private final MemberRepository memberRepository;
    private final DailyPlanRepository dailyPlanRepository;

    public RateInfoResponse getRateInfo(String memberId, LocalDate date){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(member.getId(), date)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND));

        return RateConverter.toRateInfoResponse(dailyPlan);
    }

    @Transactional
    public void updateRate(String memberId, LocalDate date, RateUpdateRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(member.getId(), date)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND));

        dailyPlan.updateRate(request.getRateMemo(), Rate.valueOf(request.getRate()));
    }

}
