package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class ExpenseException extends CustomException{
    public ExpenseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ExpenseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
