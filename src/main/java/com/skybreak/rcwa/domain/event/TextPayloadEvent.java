package com.skybreak.rcwa.domain.event;

import lombok.Builder;

@Builder
public class TextPayloadEvent {
    private final TextPayloadEventType type;
    private final String payload;
}

