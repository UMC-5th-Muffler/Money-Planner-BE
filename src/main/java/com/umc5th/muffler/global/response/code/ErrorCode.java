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
    _INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "COMMON401", "권한이 유효하지 않습니다."),

    // Member 에러
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "존재하지 않는 사용자입니다."),

    // Goal 에러
    _GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL404", "존재하지 않는 목표입니다."),
    _INVALID_GOAL_DATE(HttpStatus.BAD_REQUEST, "GOAL404", "올바르지 않은 목표 기간 입력입니다."),
    _INVALID_DAILY_PLAN(HttpStatus.BAD_REQUEST, "GOAL404", "올바르지 않은 일일 계획 입력입니다."),

    // Member 에러
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER400", "등록된 회원이 없습니다."),

    // Category 에러
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "CATEGORY400", "카테고리 이름이 중복되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
