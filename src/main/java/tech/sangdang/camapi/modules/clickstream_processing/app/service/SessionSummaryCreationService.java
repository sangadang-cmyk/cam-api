package tech.sangdang.camapi.modules.clickstream_processing.app.service;

import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.command.CreateSessionSummaryFromEventCommand;

public interface SessionSummaryCreationService {
    Mono<Void> createSessionSummaryFromEvent(CreateSessionSummaryFromEventCommand command);
}
