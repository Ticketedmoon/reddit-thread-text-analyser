package com.skybreak.rcwa.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class JobCreated {
    private UUID jobId;
    private String message;
    private int status;
}
