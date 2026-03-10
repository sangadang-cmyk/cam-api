package tech.sangdang.camapi.modules.clickstream_processing.app.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class FindByIdSessionSummaryQuery {
    
    @NotBlank
    private String id;
}
