package tech.sangdang.camapi.modules.clickstream_ingestion.infra;

import com.azure.data.schemaregistry.SchemaRegistryAsyncClient;
import com.azure.data.schemaregistry.SchemaRegistryClient;
import com.azure.data.schemaregistry.SchemaRegistryClientBuilder;
import com.azure.identity.DefaultAzureCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ClickstreamEventHubSchemaRegistryConfig {
    private final ClickstreamEventHubProperties clickstreamEventHubProperties;
    private final DefaultAzureCredential defaultAzureCredential;
    
    @Bean
    SchemaRegistryClient schemaRegistryClient() {
        return new SchemaRegistryClientBuilder()
                .fullyQualifiedNamespace(clickstreamEventHubProperties.getEventHubFullyQualifiedNamespace())
                .credential(defaultAzureCredential)
                .buildClient();
    }
}
