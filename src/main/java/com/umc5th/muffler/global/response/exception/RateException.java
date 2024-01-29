package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class RateException extends CustomException{
    public RateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
