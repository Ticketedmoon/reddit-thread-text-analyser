package com.skybreak.rcwa.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TextPayloadEvent implements Serializable {
    private final TextPayloadEventType type;
    private final String payload;
}

