package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpenseFixture {
    public static Expense create(LocalDate date) {
        return Expense.builder()
                .id(1L)
                .date(date)
                .title("title")
                .cost(100L)
                .memo("memo")
                .member(MemberFixture.MEMBER_ONE)
                .category(CategoryFixture.CATEGORY_ONE)
                .build();
    }

    public static List<Expense> createList(int num){
        Member member = Member.builder().id(1L).build();
        Category category = Category.builder().id(1L).icon("icon").build();

        return IntStream.rangeClosed(1, num)
                .mapToObj(i -> Expense.builder()
                        .date(LocalDate.of(2024, 1, 1))
                        .title("title")
                        .cost(100L)
                        .memo("memo")
                        .member(member)
                        .category(category)
                        .build())
                        .collect(Collectors.toList());
    }
}
