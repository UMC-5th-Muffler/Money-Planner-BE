package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class CommonException extends CustomException {
    public CommonException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public CommonException(ErrorCode errorCode) {
        super(errorCode);
    }
}
