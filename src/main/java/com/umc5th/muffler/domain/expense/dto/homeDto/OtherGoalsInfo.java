package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OtherGoalsInfo {

    private LocalDate otherStartDate;
    private LocalDate otherEndDate;
    private List<Level> totalLevelList;
}
