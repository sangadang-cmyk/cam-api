package tech.sangdang.camapi.modules.clickstream_processing.domain;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionSummaryRepository extends ReactiveCosmosRepository<SessionSummary, String> {
}
