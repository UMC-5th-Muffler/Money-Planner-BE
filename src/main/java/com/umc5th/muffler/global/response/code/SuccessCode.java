package com.umc5th.muffler.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    OK(HttpStatus.OK, "성공입니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
