package tech.sangdang.camapi.modules.clickstream_ingestion.infra;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "custom.azure.eventhubs.clickstream")
public class ClickstreamEventHubProperties {
    /**
     * Namespace of the eventhub without the ".servicebus.windows.net" suffix, e.g. "my-eventhub-namespace"
     */
    private @NotBlank String eventHubNamespace;
    private @NotBlank String eventHubName;
    private String consumerGroup = "$Default";
    private @NotBlank String storageAccountEndpoint;
    private @NotBlank String storageContainerName;
    private @NotBlank String schemaGroupName;
    private String schemaName = "ClickstreamEvent";
    private Integer schemaVersion = 1;
    
    public String getEventHubFullyQualifiedNamespace() {
        return eventHubNamespace + ".servicebus.windows.net";
    }
}
