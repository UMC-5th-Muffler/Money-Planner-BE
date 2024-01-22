package com.umc5th.muffler.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc5th.muffler.global.response.Response;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        Response<Void> body = Response.error(message);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(body);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    public static <T> void sendSuccessResponse(HttpServletResponse response, int statusCode, T data) throws IOException {
        response.setStatus(statusCode);
        Response<T> body = Response.success(data);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(body);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
