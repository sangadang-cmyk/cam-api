package tech.sangdang.camapi.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "custom.azure")
public class AzureConfigProperties {
    private @NotBlank String fullAccessClientId;
    private Boolean disableEventConsumer = true;
}
