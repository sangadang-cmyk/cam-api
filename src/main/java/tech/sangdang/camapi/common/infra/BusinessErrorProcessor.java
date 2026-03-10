package tech.sangdang.camapi.common.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import tech.sangdang.camapi.common.core.BusinessError;
import tech.sangdang.camapi.common.core.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class BusinessErrorProcessor {
    @ExceptionHandler(BusinessError.class)
    public ResponseEntity<ErrorResponse> handleBusinessError(BusinessError ex, ServerWebExchange request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequest().getURI().getPath())
                .build();

        return new ResponseEntity<>(error, ex.getHttpStatus());
    }
}
