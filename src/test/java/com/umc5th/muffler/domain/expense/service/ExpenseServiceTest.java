package com.umc5th.muffler.domain.expense.service;

import static org.junit.jupiter.api.Assertions.*;

import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.global.response.exception.CustomException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseServiceTest {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseServiceTest(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Test
    @Transactional
    public void 정상_입력() {
        // given
        NewExpenseRequest req = new NewExpenseRequest(1L, "하이", 100L,
                LocalDate.of(2024, 1, 13), null, "카테고리");
        // when
        NewExpenseResponse res = expenseService.enrollExpense(req);
        // then
        assertEquals(req.getExpenseCost(), res.getCost());
    }

    @Test
    @Transactional
    public void 등록_안된_카테고리의_경우() {
        // given
        NewExpenseRequest req = new NewExpenseRequest(1L, "하이", 100L,
                LocalDate.of(2024, 1, 13), null, "카테고리1");

        //then
        assertThrows(CustomException.class, () -> {
            expenseService.enrollExpense(req);
        });
    }

    @Test
    @Transactional
    public void 멤버가_등록_안된_경우() {
        // given
        NewExpenseRequest req = new NewExpenseRequest(2L, "하이", 100L,
                LocalDate.of(2024, 1, 13), null, "카테고리1");

        //then
        assertThrows(CustomException.class, () -> {
            expenseService.enrollExpense(req);
        });
    }

}