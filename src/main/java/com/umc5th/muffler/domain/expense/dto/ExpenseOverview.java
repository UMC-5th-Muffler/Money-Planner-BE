package com.umc5th.muffler.domain.expense.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExpenseOverview {
    private List<ZeroDayInfo> overview;
}
