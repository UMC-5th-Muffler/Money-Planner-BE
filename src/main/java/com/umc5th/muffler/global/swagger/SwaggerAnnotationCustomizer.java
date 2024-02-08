package com.umc5th.muffler.global.swagger;

import com.umc5th.muffler.global.response.code.ErrorCode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.HashMap;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SwaggerAnnotationCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        enrollErrorResponse(operation, handlerMethod);
        return operation;
    }

    private void enrollErrorResponse(Operation operation, HandlerMethod handlerMethod) {
        if (handlerMethod.hasMethodAnnotation(ErrorResponses.class)) {
            ApiResponses apiResponses = operation.getResponses();
            ErrorResponses errorResponses = handlerMethod.getMethodAnnotation(ErrorResponses.class);
            Map<Integer, Integer> statusMap = new HashMap<>();

            ErrorCode[] errorCodes = errorResponses.value();
            for (ErrorCode errorCode : errorCodes) {
                int code = errorCode.getHttpStatus().value();
                StringBuilder sb = new StringBuilder()
                        .append(code);
                ApiResponse apiResponse = new ApiResponse()
                        .description(errorCode.getMessage());

                if (statusMap.containsKey(code)) {
                    Integer num = statusMap.get(code);
                    sb.append("_").append(num);
                    statusMap.put(code, num + 1);
                }else {
                    statusMap.put(code, 1);
                }
                apiResponses.addApiResponse(sb.toString(), apiResponse);
            }
        }
    }
}
