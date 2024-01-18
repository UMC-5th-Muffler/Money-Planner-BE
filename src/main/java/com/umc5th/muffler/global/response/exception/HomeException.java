package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class HomeException extends CustomException {

    public HomeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public HomeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
