package com.skybreak.rcwa.application.controller;

import com.skybreak.rcwa.application.service.DataExtractionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobManagementController {

    private static final String JOB_STARTED_MESSAGE = "Word Analysis job started for subreddit [%s] - scanning " +
            "the title text and comment text from the top [%d] posts";
    private static final String TOTAL_POSTS_VALIDATION_EXCEPTION_MESSAGE = "parameter `totalPosts` must be less than or equal to 100";
    private final DataExtractionProducer dataExtractionProducer;

    // TODO Make me more inline with restful principles (PUT/POST + startJob: true or similar)
    @GetMapping("/start-report")
    public ResponseEntity<String> startReport(@RequestParam String subreddit,
                                              @RequestParam(defaultValue = "100")
                                              @Max(value = 100, message = TOTAL_POSTS_VALIDATION_EXCEPTION_MESSAGE) int totalPosts) {
        dataExtractionProducer.startJob(subreddit, totalPosts);
        return ResponseEntity.accepted().body(String.format(JOB_STARTED_MESSAGE, subreddit, totalPosts));
    }
}