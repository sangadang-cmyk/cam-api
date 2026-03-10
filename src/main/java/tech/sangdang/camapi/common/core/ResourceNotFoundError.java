package tech.sangdang.camapi.common.core;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundError extends BusinessError {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundError(String resourceName, String id) {
        super(resourceName + " not found with id: " + id);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}

