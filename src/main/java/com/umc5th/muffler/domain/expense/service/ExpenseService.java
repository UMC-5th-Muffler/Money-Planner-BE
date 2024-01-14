package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.converter.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public DailyExpenseDetailsResponse getDailyExpenseDetails(LocalDate date, Pageable pageable){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode._MEMBER_NOT_FOUND));

        Slice<Expense> expenseList = expenseRepository.findAllByMemberAndDate(member, date, pageable);
        
        List<Category> categoryList = categoryRepository.findAllByMember(member); // member와 연관된 카테고리 리스트

        DailyExpenseDetailsResponse response = ExpenseConverter.toDailyExpenseDetail(expenseList, categoryList, date);

        return response;
    }


}
