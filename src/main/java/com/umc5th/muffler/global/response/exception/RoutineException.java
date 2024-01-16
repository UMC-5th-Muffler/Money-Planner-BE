package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class RoutineException extends CustomException {

    public RoutineException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RoutineException(ErrorCode errorCode) {
        super(errorCode);
    }

}
