package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class MailException extends CustomException{
    public MailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public MailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
