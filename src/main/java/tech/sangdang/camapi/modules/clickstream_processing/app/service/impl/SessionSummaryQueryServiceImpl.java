package tech.sangdang.camapi.modules.clickstream_processing.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.common.core.ResourceNotFoundError;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindAllSessionSummariesQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.query.FindByIdSessionSummaryQuery;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.res.SessionSummaryResponse;
import tech.sangdang.camapi.modules.clickstream_processing.app.mapper.SessionSummaryMapper;
import tech.sangdang.camapi.modules.clickstream_processing.app.service.SessionSummaryQueryService;
import tech.sangdang.camapi.modules.clickstream_processing.domain.SessionSummaryRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionSummaryQueryServiceImpl implements SessionSummaryQueryService {
    
    private final SessionSummaryRepository repository;
    private final SessionSummaryMapper mapper;
    
    @Override
    public Mono<SessionSummaryResponse> findById(FindByIdSessionSummaryQuery query) {
        log.debug("Finding session summary by id: {}", query.getId());
        
        return repository.findById(query.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundError("SessionSummary", query.getId())))
                .map(mapper::toResponse);
    }
    
    @Override
    public Flux<SessionSummaryResponse> findAll(FindAllSessionSummariesQuery query) {
        log.debug("Finding all session summaries");
        
        return repository.findAll()
                .map(mapper::toResponse);
    }
}
