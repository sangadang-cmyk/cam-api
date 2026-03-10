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
import reactor.core.publisher.Mono;

import java.util.Collections;

@Tag(name = "_Test")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class ClickstreamEventPublishingController {
    private final EventHubProducerAsyncClient producerClient;
    
    @Operation(summary = "Publish a test clickstream event to the Event Hub. This endpoint is for testing purposes only.")
    @PostMapping("/clickstream/publish")
    public Mono<Void> publishTestEvent() {
        return producerClient.send(
                Collections.singletonList(
                        new EventData("Test clickstream event at " + System.currentTimeMillis())
                )
        );
    }
    
    @Operation(summary = "Publish multile test clickstream events to the Event Hub. This endpoint is for testing purposes only.")
    @PostMapping("/clickstream/publish/batch")
    public Mono<Void> publishBatchTestEvents(@RequestParam(defaultValue = "10") Integer count) {
        var events = Collections.nCopies(count, "Batch test clickstream event at " + System.currentTimeMillis())
                .stream()
                .map(EventData::new)
                .toList();
        return producerClient.send(events);
    }
}
