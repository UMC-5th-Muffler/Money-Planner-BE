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
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "존재하지 않는 사용자입니다."),

    // Routine 에러
    INVALID_ROUTINE_END_DATE(HttpStatus.BAD_REQUEST, "ROUTINE4001", "루틴 설정이 불가능한 날이 포함되어 있습니다."),

    // Goal 에러
    GOAL_NOT_FOUND(HttpStatus.BAD_REQUEST, "GOAL4001", "존재하지 않는 목표입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
