package tech.sangdang.camapi.modules.clickstream_ingestion.app.service.impl;

import com.azure.messaging.eventhubs.EventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_ingestion.app.service.ClickstreamEventIngestionService;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.command.CreateSessionSummaryFromEventCommand;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryCreationService;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClickstreamEventIngestionServiceImpl implements ClickstreamEventIngestionService {
    private final SessionSummaryCreationService sessionSummaryCreationService;
    private final ObjectMapper objectMapper;
    private final JsonSchema clickstreamEventSchema;

    @Override
    public Mono<Void> handleClickstreamEvent(EventData eventData) {
        try {
            log.info("Details: Received event from partition with sequence number {} and body: {}",
                    eventData.getSequenceNumber(),
                    eventData.getBodyAsString());

            if(clickstreamEventSchema != null) {
                String json = eventData.getBodyAsString();
                Set<ValidationMessage> validationResult = clickstreamEventSchema.validate(objectMapper.readTree(json));
                if (!validationResult.isEmpty()) {
                    log.error("Event data validation failed: {}", validationResult);
                    return Mono.empty();
                }
            }

            EventDataDto eventDataDto = objectMapper.readValue(eventData.getBodyAsString(), EventDataDto.class);

            var command = CreateSessionSummaryFromEventCommand.builder()
                    .eventType(eventDataDto.getEventType())
                    .pageUrl(eventDataDto.getPageUrl())
                    .timestamp(eventDataDto.getTimestamp())
                    .sessionId(eventDataDto.getSessionId())
                    .userId(eventDataDto.getUserId())
                    .build();
            return sessionSummaryCreationService.createSessionSummaryFromEvent(command);
        } catch (Exception e) {
            log.error("Failed to process event: {}", e.getMessage(), e);
            return Mono.empty();
        }
    }

    @SuperBuilder(toBuilder = true)
    @Data
    @NoArgsConstructor
    public static class EventDataDto {
        private String userId;
        private String sessionId;
        private String eventType;
        private String pageUrl;
        private Long timestamp;
    }
}
