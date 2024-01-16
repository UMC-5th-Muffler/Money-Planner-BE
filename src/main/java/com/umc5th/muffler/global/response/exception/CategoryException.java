package com.umc5th.muffler.global.response.exception;

import com.umc5th.muffler.global.response.code.ErrorCode;

public class CategoryException extends CustomException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
    public CategoryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
