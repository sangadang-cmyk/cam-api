package tech.sangdang.camapi.common.core;

import org.springframework.http.HttpStatus;

public abstract class BusinessError extends RuntimeException {
    public abstract HttpStatus getHttpStatus();
    public abstract String getErrorCode();

    public BusinessError() {
    }

    public BusinessError(String message) {
        super(message);
    }

    public BusinessError(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessError(Throwable cause) {
        super(cause);
    }

    public BusinessError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
