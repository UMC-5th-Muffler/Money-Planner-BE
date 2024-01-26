package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpenseFixture {
  
  public static Expense EXPENSE_ONE = Expense.builder()
            .id(1L)
            .title("ONE")
            .cost(1L)
            .memo("ONE MEMO")
            .date(LocalDate.of(2024, 1, 1))
            .build();
    public static Expense EXPENSE_TWO = Expense.builder()
            .id(2L)
            .title("TWO")
            .cost(2L)
            .memo("TWO MEMO")
            .date(LocalDate.of(2024, 1, 12))
            .build();
    public static Expense EXPENSE_THREE = Expense.builder()
            .id(3L)
            .title("THREE")
            .cost(3L)
            .memo("THREE MEMO")
            .date(LocalDate.of(2024, 1, 31))
            .build();
  
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

    public static List<Expense> createList(int num, LocalDate date){
        Member member = Member.builder().id("1").build();
        Category category = Category.builder().id(1L).icon("icon").build();

        return IntStream.rangeClosed(1, num)
                .mapToObj(i -> {
                    Expense expense = Expense.builder()
                            .id(Long.valueOf(i))
                            .date(date)
                            .title("title")
                            .cost(100L)
                            .memo("memo")
                            .member(member)
                            .category(category)
                            .build();
                    // createdAt 오름차순으로 생성
                    ReflectionTestUtils.setField(expense, "createdAt", LocalDateTime.now().plusHours(i));
                    return expense;
                })
                .collect(Collectors.toList());
    }

    public static List<Expense> createListWithStartNum(int start, int num, LocalDate date){
        Member member = Member.builder().id("1").build();
        Category category = Category.builder().id(1L).icon("icon").build();

        return IntStream.rangeClosed(start, start+num-1)
                .mapToObj(i -> {
                    Expense expense = Expense.builder()
                            .id(Long.valueOf(i))
                            .date(date)
                            .title("title")
                            .cost(100L)
                            .memo("memo")
                            .member(member)
                            .category(category)
                            .build();
                    // createdAt 오름차순으로 생성
                    ReflectionTestUtils.setField(expense, "createdAt", LocalDateTime.now().plusHours(i));
                    return expense;
                })
                .collect(Collectors.toList());
    }
}
