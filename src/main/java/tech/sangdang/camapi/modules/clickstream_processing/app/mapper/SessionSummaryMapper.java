package tech.sangdang.camapi.modules.clickstream_processing.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import tech.sangdang.camapi.modules.clickstream_processing.app.dto.res.SessionSummaryResponse;
import tech.sangdang.camapi.modules.clickstream_processing.domain.SessionSummary;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SessionSummaryMapper {
    SessionSummaryResponse toResponse(SessionSummary entity);
}
