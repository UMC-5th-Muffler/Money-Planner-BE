package com.umc5th.muffler.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc5th.muffler.global.response.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class Response<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(Include.NON_NULL)
    private T result;

    public static Response<Void> success() {
        return new Response<>(true, SuccessCode._OK.getCode(), SuccessCode._OK.getMessage(), null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(true, SuccessCode._OK.getCode(), SuccessCode._OK.getMessage(), result);
    }

    public static Response<Void> error(String code, String message) {
        return new Response<>(false, code, message, null);
    }
}
