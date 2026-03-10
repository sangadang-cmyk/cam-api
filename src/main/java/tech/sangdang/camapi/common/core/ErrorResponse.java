package tech.sangdang.camapi.common.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    LocalDateTime timestamp;
    Integer status;
    String error;
    String message;
    String path;
}
