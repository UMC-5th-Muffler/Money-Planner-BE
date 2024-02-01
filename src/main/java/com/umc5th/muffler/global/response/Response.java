package com.umc5th.muffler.global.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc5th.muffler.global.response.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "message", "result"})
public class Response<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private T result;

    public static Response<Void> success() {
        return new Response<>(true, SuccessCode.OK.getMessage(), null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(true, SuccessCode.OK.getMessage(), result);
    }

    public static Response<Void> error(String message) {
        return new Response<>(false, message, null);
    }

    public static <T> Response<T> error(String message, T result) {
        return new Response<>(false, message, result);
    }
}
