package tech.sangdang.camapi.modules.clickstream_processing.app.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionSummaryResponse {
    private String id;
    private String userId;
    private Integer pageCount;
    private Integer durationSec;
    private LocalDateTime firstEventTime;
    private LocalDateTime lastEventTime;
    private LocalDateTime processedAt;
}
