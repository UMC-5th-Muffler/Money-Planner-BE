package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.repository.RateRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final RateRepository rateRepository;
    private final GoalRepository goalRepository;


    public List<Category> getEvalCategoryList(LocalDate date){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // member -> 오늘 날짜에 해당하는 goal을 찾는다 -> 해당 goal과 연관된 goalCategory 가져오기 -> goalCategory와 연관된 Category 이름
        Goal goal = goalRepository.findByMemberAndStartDateLessThanEqualAndEndDateGreaterThanEqual(member, date);


    }
}
