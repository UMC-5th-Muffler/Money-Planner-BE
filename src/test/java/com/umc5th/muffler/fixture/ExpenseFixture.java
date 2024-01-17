package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpenseFixture {
    public static List<Expense> createList(int num, LocalDate date){
        Member member = Member.builder().id(1L).build();
        Category category = Category.builder().id(1L).icon("icon").build();

        return IntStream.rangeClosed(1, num)
                .mapToObj(i -> Expense.builder()
                        .date(date)
                        .title("title")
                        .cost(100L)
                        .memo("memo")
                        .member(member)
                        .category(category)
                        .build())
                        .collect(Collectors.toList());
    }
}
