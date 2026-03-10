package tech.sangdang.camapi.modules.clickstream_ingestion.api;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_ingestion.app.service.impl.ClickstreamEventIngestionServiceImpl;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Tag(name = "_Test")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestClickstreamEventPublishingController {
    private final EventHubProducerAsyncClient producerClient;
    public static final String[] SAMPLE_EVENT_TYPES = {"page_view", "click", "scroll", "form_submit"};
    public static final String[] SAMPLE_PAGE_URLS = {
            "https://example.com/home",
            "https://example.com/product/123",
            "https://example.com/search?q=shoes",
            "https://example.com/profile",
            "https://example.com/cart",
            "https://example.com/checkout"
    };
    public static final Map<String, String> SAMPLE_SESSION_IDS = Map.of(
            "session1", "user1",
            "session2", "user2",
            "session3", "user3",
            "session4", "user4",
            "session5", "user5"
    );
    
    private final ObjectMapper objectMapper;
    
    @Operation(summary = "Publish events")
    @PostMapping("/publish")
    public Mono<Void> publishEvents(@RequestParam("count") int count) {
        long time = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(i -> run(time, i))
                .then();
    }
    
    Mono<Void> run(long startTime, int iteration) {
        var randomSession = SAMPLE_SESSION_IDS.entrySet().stream()
                .skip(new Random().nextInt(SAMPLE_SESSION_IDS.size()))
                .findFirst()
                .orElseThrow();
        var eventDataDto = ClickstreamEventIngestionServiceImpl.EventDataDto.builder()
                .eventType(SAMPLE_EVENT_TYPES[new Random().nextInt(SAMPLE_EVENT_TYPES.length)])
                .pageUrl(SAMPLE_PAGE_URLS[new Random().nextInt(SAMPLE_PAGE_URLS.length)])
                .timestamp(startTime + iteration * 3000L) // 3 second apart
                .sessionId(randomSession.getKey())
                .userId(randomSession.getValue())
                .build();

        String eventBody = objectMapper.writeValueAsString(eventDataDto);
        EventData eventData = new EventData(eventBody);
        return producerClient.send(Collections.singletonList(eventData))
                .doOnSuccess(_ -> log.info("Published event: {}", eventBody))
                .doOnError(error -> log.error("Failed to publish event: {}", eventBody, error));
    }
}
