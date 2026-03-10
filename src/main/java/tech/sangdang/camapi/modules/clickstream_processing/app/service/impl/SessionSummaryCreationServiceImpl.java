package tech.sangdang.camapi.modules.clickstream_processing.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.command.CreateSessionSummaryFromEventCommand;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryCreationService;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionSummaryCreationServiceImpl implements SessionSummaryCreationService {
    @Override
    public Mono<Void> createSessionSummaryFromEvent(CreateSessionSummaryFromEventCommand command) {
        return Mono.empty();
    }
}
