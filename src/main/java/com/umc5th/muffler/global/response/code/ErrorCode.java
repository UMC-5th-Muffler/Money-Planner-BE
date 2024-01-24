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
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),


    // Member 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),


    // Goal 에러
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 목표입니다."),
    INVALID_GOAL_INPUT(HttpStatus.BAD_REQUEST, "올바르지 않은 목표 입력입니다."),
    NO_GOAL_IN_GIVEN_DATE(HttpStatus.BAD_REQUEST, "해당 날짜에 일치하는 목표가 없습니다"),


    // Category 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "카테고리 이름이 중복되었습니다."),
    ACCESS_TO_OTHER_USER_CATEGORY(HttpStatus.UNAUTHORIZED, "다른 사람의 카테고리 아이디입니다."),

    // Expense 에러
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 소비 내역입니다."),


    // Routine 에러
    INVALID_ROUTINE_INPUT(HttpStatus.BAD_REQUEST, "올바르지 않은 루틴 입력입니다."),
    ROUTINE_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "루틴 타입이 없거나 유효하지 않습니다."),
    ALREADY_INACTIVE_CATEGORY(HttpStatus.BAD_REQUEST, "이미 삭제된 카테고리 입니다."),
    CANNOT_DELETE_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST,"기본 카테고리는 삭제할 수 없습니다."),
    CANNOT_UPDATE_DEFAULT_ICON(HttpStatus.BAD_REQUEST, "디폴트 카테고리는 아이콘을 수정할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}