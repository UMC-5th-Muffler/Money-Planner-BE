package com.umc5th.muffler.domain.member.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmAgreeUpdateRequest {
    @NotNull private Boolean dailyPlanRemindAgree;
    @NotNull private Boolean todayEnrollRemindAgree;
    @NotNull private Boolean yesterdayEnrollRemindAgree;
    @NotNull private Boolean goalEndRemindAgree;
}
