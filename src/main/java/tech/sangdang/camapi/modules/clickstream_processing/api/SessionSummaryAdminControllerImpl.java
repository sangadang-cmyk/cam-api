package tech.sangdang.camapi.modules.clickstream_processing.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindAllSessionSummariesQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindByIdSessionSummaryQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.res.SessionSummaryResponse;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryQueryService;

@RestController
@RequiredArgsConstructor
public class SessionSummaryAdminControllerImpl implements SessionSummaryAdminController {
    
    private final SessionSummaryQueryService queryService;
    
    @Override
    public Mono<SessionSummaryResponse> findById(String id) {
        FindByIdSessionSummaryQuery query = FindByIdSessionSummaryQuery.builder()
                .id(id)
                .build();
        return queryService.findById(query);
    }
    
    @Override
    public Flux<SessionSummaryResponse> findAll() {
        FindAllSessionSummariesQuery query = FindAllSessionSummariesQuery.builder()
                .build();
        return queryService.findAll(query);
    }
}
