package tech.sangdang.camapi.modules.clickstream_ingestion.infra;

import com.azure.identity.DefaultAzureCredential;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.sangdang.camapi.modules.clickstream_ingestion.app.service.ClickstreamEventIngestionService;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ClickstreamEventHubConfig {
    private final ClickstreamEventHubProperties properties;
    private final DefaultAzureCredential defaultAzureCredential;
    private final ClickstreamEventIngestionService clickstreamEventIngestionService;

    /**
     * Create a builder for producing events
     */
    @Bean
    EventHubClientBuilder eventHubClientBuilder() {
        return new EventHubClientBuilder().credential(
                properties.getEventHubFullyQualifiedNamespace(),
                properties.getEventHubName(),
                defaultAzureCredential
        );
    }

    /**
     * Create a builder for processing events 
     */
    @Bean
    EventProcessorClientBuilder eventProcessorClientBuilder(BlobContainerAsyncClient blobContainerAsyncClient) {
        return new EventProcessorClientBuilder()
                .credential(
                        properties.getEventHubFullyQualifiedNamespace(),
                        properties.getEventHubName(),
                        defaultAzureCredential
                )
                .consumerGroup(properties.getConsumerGroup())
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient))
                .processEvent(eventContext -> {
                    log.info("Received event: {}", eventContext.toString());
                    clickstreamEventIngestionService.handleClickstreamEvent(eventContext.getEventData())
                            .doOnSuccess(unused -> {
                                log.info("Processing successful");
                            })
                            .then(eventContext.updateCheckpointAsync())
                            .block();
                })
                .processError(errorContext -> {
                    log.error("Received error");
                    log.error(errorContext.toString());
                });
    }

    /**
     * Create a builder for the checkpointing blob container
     */
    @Bean
    BlobContainerClientBuilder blobContainerClientBuilder() {
        return new BlobContainerClientBuilder()
                .credential(defaultAzureCredential)
                .endpoint(properties.getStorageAccountEndpoint())
                .containerName(properties.getStorageContainerName());
    }

    /**
     * Create the client for checkpointing 
     */
    @Bean
    BlobContainerAsyncClient blobContainerAsyncClient(BlobContainerClientBuilder blobContainerClientBuilder) {
        return blobContainerClientBuilder.buildAsyncClient();
    }

    /**
     * Create a client for producing events using the aforementioned builder
     */
    @Bean
    EventHubProducerAsyncClient eventHubProducerAsyncClient(EventHubClientBuilder eventHubClientBuilder) {
        return eventHubClientBuilder.buildAsyncProducerClient();
    }

    /**
     * Create a client for processing events using the aforementioned builder
     */
    @Bean
    EventProcessorClient eventProcessorClient(EventProcessorClientBuilder eventProcessorClientBuilder) {
        return eventProcessorClientBuilder.buildEventProcessorClient();
    }
}
