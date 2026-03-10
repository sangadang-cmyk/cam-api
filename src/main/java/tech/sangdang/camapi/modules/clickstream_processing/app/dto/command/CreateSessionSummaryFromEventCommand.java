package tech.sangdang.camapi.modules.clickstream_processing.app.dto.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CreateSessionSummaryFromEventCommand {
    private String userId;
    private String sessionId;
    private String eventType;
    private String pageUrl;
    private Long timestamp;
}
