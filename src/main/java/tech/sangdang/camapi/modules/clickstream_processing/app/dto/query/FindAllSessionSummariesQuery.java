package tech.sangdang.camapi.modules.clickstream_processing.app.dto.query;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class FindAllSessionSummariesQuery {
    // Can add pagination, filtering, sorting parameters here in the future
    // For now, this will return all session summaries
}
