package tech.sangdang.camapi.modules.clickstream_processing.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.res.SessionSummaryResponse;

@Tag(name = "Session Summary Admin")
@RequestMapping("/api/admin/session-summary")
public interface SessionSummaryAdminController {
    @Operation(summary = "Get a session summary by ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<SessionSummaryResponse> findById(@PathVariable String id);
    
    @Operation(summary = "Get all session summaries")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Flux<SessionSummaryResponse> findAll();
}
