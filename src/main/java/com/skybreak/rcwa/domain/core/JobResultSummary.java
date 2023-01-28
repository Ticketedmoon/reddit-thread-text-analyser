package com.skybreak.rcwa.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonPropertyOrder({ "job_execution_metadata", "results" })
public class JobResultSummary {

    @JsonProperty("job_execution_metadata")
    private JobExecutionMetadata jobExecutionMetadata;

    private Map<TextPayloadEventType, List<UserThreadTextItem>> results;

}
