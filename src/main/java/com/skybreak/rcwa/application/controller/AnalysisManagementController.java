package com.skybreak.rcwa.application.controller;

import com.skybreak.rcwa.application.service.AnalysisManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisManagementController {

    private static final String JOB_STARTED_MESSAGE = "Word Analysis job started for subreddit [%s] - scanning " +
            "the title text and comment text from the top [%d] posts";

    private final AnalysisManagementService analysisManagementService;

    @GetMapping("/start-analysis")
    public ResponseEntity<String> startAnalysis(@RequestParam String subreddit,
                                                @RequestParam(defaultValue = "100") int totalPosts) {
        analysisManagementService.startJob(subreddit, totalPosts);
        return ResponseEntity.ok().body(String.format(JOB_STARTED_MESSAGE, subreddit, totalPosts));
    }
}