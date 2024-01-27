package com.umc5th.muffler.domain.dailyplan.service;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanConverter;
import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyPlanService {

    private final DailyPlanRepository dailyPlanRepository;

    public RateInfoResponse getRateInfo(LocalDate date){
        DailyPlan dailyPlan = dailyPlanRepository.findByDate(date)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND));

        return DailyPlanConverter.toRateInfoResponse(dailyPlan);
    }

    @Transactional
    public void updateRate(LocalDate date, RateUpdateRequest request){
        DailyPlan dailyPlan = dailyPlanRepository.findByDate(date)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.DAILYPLAN_NOT_FOUND));

        dailyPlan.updateRate(request.getMemo(), Level.valueOf(request.getRate()));
    }

}
