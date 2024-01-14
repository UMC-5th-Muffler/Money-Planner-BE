package com.umc5th.muffler.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 일반 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Member 에러
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER400", "등록된 회원이 없습니다."),

    //Goal 에러
    _NO_GOAL_IN_GIVEN_DATE(HttpStatus.BAD_REQUEST, "GOAL400", "해당 날짜에 일치하는 목표가 없습니다"),

    // Category 에러
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "CATEGORY400", "카테고리 이름이 중복되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
