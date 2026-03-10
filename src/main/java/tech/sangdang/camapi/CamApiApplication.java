package tech.sangdang.camapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import tech.sangdang.camapi.config.AzureConfigProperties;
import tech.sangdang.camapi.modules.clickstream_ingestion.infra.ClickstreamEventHubProperties;

@ConfigurationPropertiesScan(basePackageClasses = {ClickstreamEventHubProperties.class, AzureConfigProperties.class})
@SpringBootApplication
public class CamApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamApiApplication.class, args);
    }

}
