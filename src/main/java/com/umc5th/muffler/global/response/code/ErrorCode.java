package com.umc5th.muffler.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 일반 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "금지된 요청입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "권한이 유효하지 않습니다."),


    // Member 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),


    // Goal 에러
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 목표입니다."),
    INVALID_GOAL_DATE(HttpStatus.BAD_REQUEST, "올바르지 않은 목표 기간 입력입니다."),
    INVALID_DAILY_PLAN(HttpStatus.BAD_REQUEST, "올바르지 않은 일일 계획 입력입니다."),


    // Category 에러
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "카테고리 이름이 중복되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
