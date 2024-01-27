package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class DailyPlanException  extends CustomException{
    public DailyPlanException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DailyPlanException(ErrorCode errorCode) {
        super(errorCode);
    }
}