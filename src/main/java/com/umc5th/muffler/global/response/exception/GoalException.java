package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class GoalException extends CustomException {
    public GoalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public GoalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
