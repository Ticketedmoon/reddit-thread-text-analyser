package com.skybreak.rcwa.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
public class TextPayloadEvent implements Serializable {
    private UUID jobId;
    private final TextPayloadEventType type;
    private final String payload;
}

