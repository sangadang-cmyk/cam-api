package tech.sangdang.camapi.modules.clickstream_processing.domain;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Container(containerName = "session_summaries")
public class SessionSummary {
    @Id
    private String id;
    @PartitionKey
    private String userId;
    private Integer pageCount;
    private Integer durationSec;
    private LocalDateTime firstEventTime;
    private LocalDateTime lastEventTime;
    @Builder.Default
    private LocalDateTime processedAt = LocalDateTime.now();
    @Version
    private String _etag;
}
