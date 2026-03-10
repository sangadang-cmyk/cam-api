package tech.sangdang.camapi.modules.clickstream_ingestion.infra.schemas;

import com.azure.data.schemaregistry.SchemaRegistryClient;
import com.azure.data.schemaregistry.models.SchemaRegistrySchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.sangdang.camapi.modules.clickstream_ingestion.infra.ClickstreamEventHubProperties;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ClickstreamEventSchema {
    private final ClickstreamEventHubProperties clickstreamEventHubProperties;

    @Bean
    JsonSchema clickstreamEventJsonSchema(SchemaRegistryClient client) {
        if(clickstreamEventHubProperties.getSchemaGroupName() == null) {
            log.warn("Schema group name is not configured, clickstream event JSON schema will not be loaded");
            return null;
        }
        
        SchemaRegistrySchema schema = client.getSchema(
                clickstreamEventHubProperties.getSchemaGroupName(),
                clickstreamEventHubProperties.getSchemaName(),
                clickstreamEventHubProperties.getSchemaVersion()
        );
        return JsonSchemaFactory
                .getInstance(SpecVersion.VersionFlag.V202012)
                .getSchema(schema.getDefinition());
    }
}
