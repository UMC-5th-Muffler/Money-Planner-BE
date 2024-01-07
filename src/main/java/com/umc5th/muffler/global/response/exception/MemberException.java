package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
