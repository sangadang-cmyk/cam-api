package tech.sangdang.camapi.modules.clickstream_processing.app.service.impl;

import com.azure.spring.data.cosmos.exception.CosmosAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.command.CreateSessionSummaryFromEventCommand;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryCreationService;
import tech.sangdang.camapi.modules.clickstream_processing.domain.SessionSummary;
import tech.sangdang.camapi.modules.clickstream_processing.domain.SessionSummaryRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionSummaryCreationServiceImpl implements SessionSummaryCreationService {
    private final SessionSummaryRepository sessionSummaryRepository;

    /**
     * note to self: this function needs to be idempotent
     */
    @Override
    public Mono<Void> createSessionSummaryFromEvent(CreateSessionSummaryFromEventCommand command) {
        return Mono.defer(() -> sessionSummaryRepository.findById(command.getSessionId())
                        // if session id found
                        .flatMap(existing -> {
                            existing.setPageCount(existing.getPageCount() + 1);
                            if (existing.getLastEventTime() == null || existing.getLastEventTime().isBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(command.getTimestamp()), ZoneOffset.UTC))) {
                                existing.setLastEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(command.getTimestamp()), ZoneOffset.UTC));
                                existing.setDurationSec(calculateDurationSec(existing.getFirstEventTime(), existing.getLastEventTime()));
                            }
                            return sessionSummaryRepository.save(existing);
                        })

                        // if session id not found
                        .switchIfEmpty(Mono.defer(() -> {
                            SessionSummary sessionSummary = SessionSummary.builder()
                                    .userId(command.getUserId())
                                    .pageCount(1)
                                    .firstEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(command.getTimestamp()),
                                            ZoneOffset.UTC))
                                    .lastEventTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(command.getTimestamp()),
                                            ZoneOffset.UTC))
                                    .durationSec(0)
                                    .id(command.getSessionId())
                                    .build();
                            return sessionSummaryRepository.save(sessionSummary);
                        })))
                // With this and @Version, it should retry up to 6 times when there's a version conflict. This avoids race conditions
                .retryWhen(Retry
                        .backoff(6, Duration.ofMillis(100))
                        .filter(e -> e instanceof CosmosAccessException)
                )
                .then();
    }

    private Integer calculateDurationSec(LocalDateTime firstEventTime, LocalDateTime lastEventTime) {
        return (int) (lastEventTime.toEpochSecond(ZoneOffset.UTC) - firstEventTime.toEpochSecond(ZoneOffset.UTC));
    }
}
