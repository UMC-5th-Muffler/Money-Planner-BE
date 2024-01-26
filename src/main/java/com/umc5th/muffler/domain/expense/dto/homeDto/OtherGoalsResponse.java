package com.umc5th.muffler.domain.expense.dto.homeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtherGoalsResponse {
    private List<OtherGoalsInfo> otherGoalsInfoList;
}
