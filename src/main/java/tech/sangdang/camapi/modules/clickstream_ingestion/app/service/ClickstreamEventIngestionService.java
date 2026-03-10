package tech.sangdang.camapi.modules.clickstream_ingestion.app.service;

import com.azure.messaging.eventhubs.EventData;
import reactor.core.publisher.Mono;

public interface ClickstreamEventIngestionService {
    Mono<Void> handleClickstreamEvent(EventData eventData);
}
