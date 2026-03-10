package tech.sangdang.camapi.modules.clickstream_processing.app.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindByIdSessionSummaryQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindAllSessionSummariesQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.res.SessionSummaryResponse;

@Validated
public interface SessionSummaryQueryService {
    
    Mono<SessionSummaryResponse> findById(@Valid FindByIdSessionSummaryQuery query);
    
    Flux<SessionSummaryResponse> findAll(@Valid FindAllSessionSummariesQuery query);
}
