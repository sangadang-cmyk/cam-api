package tech.sangdang.camapi.modules.clickstream_ingestion.app.service.impl;

import com.azure.messaging.eventhubs.EventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_ingestion.app.service.ClickstreamEventIngestionService;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.command.CreateSessionSummaryFromEventCommand;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryCreationService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClickstreamEventIngestionServiceImpl implements ClickstreamEventIngestionService {
    private final SessionSummaryCreationService sessionSummaryCreationService;

    @Override
    public Mono<Void> handleClickstreamEvent(EventData eventData) {
        log.info("Details: Received event from partition with sequence number {} and body: {}",
                eventData.getSequenceNumber(),
                eventData.getBodyAsString());
        return sessionSummaryCreationService.createSessionSummaryFromEvent(CreateSessionSummaryFromEventCommand.builder().build());
    }
}
