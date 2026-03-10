package tech.sangdang.camapi.config;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AzureConfig {
    private final AzureConfigProperties azureConfigProperties;
    
    @Bean
    DefaultAzureCredentialBuilder defaultAzureCredentialBuilder() {
        return new DefaultAzureCredentialBuilder()
                .managedIdentityClientId(azureConfigProperties.getFullAccessClientId());  
    }
    
    @Bean
    DefaultAzureCredential defaultAzureCredential() {
        return new DefaultAzureCredentialBuilder().build();
    }
}
