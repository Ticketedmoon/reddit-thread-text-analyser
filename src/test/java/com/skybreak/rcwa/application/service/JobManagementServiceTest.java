package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.AbstractTestContainer;
import com.skybreak.rcwa.infrastructure.persistence.JobExecutionMetadataRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class JobManagementServiceTest extends AbstractTestContainer {

    private static final UUID TEST_JOB_ID = UUID.randomUUID();
    private static final String SUBREDDIT_NAME = "test";
    private static final int DEFAULT_TOTAL_POSTS = 25;

    @Mock
    private DataExtractionProducer dataExtractionProducer;

    @Mock
    private JobExecutionMetadataRepository jobExecutionMetadataRepository;

    @InjectMocks
    private JobManagementService target;

    @AfterEach
    void teardown() {
        verifyNoMoreInteractions(dataExtractionProducer, jobExecutionMetadataRepository);
    }

    @Test
    void givenSubRedditJobRequest_whenRestClientConnects_shouldThrowAuthenticationException() throws AuthenticationException, IOException, InterruptedException {
        given(jobExecutionMetadataRepository.save(any(JobExecutionMetadata.class))).willReturn(null);
        willThrow(new AuthenticationException()).given(dataExtractionProducer).startTextExtraction(TEST_JOB_ID, SUBREDDIT_NAME, DEFAULT_TOTAL_POSTS);
        assertThatThrownBy(() -> target.startJob(TEST_JOB_ID, SUBREDDIT_NAME, DEFAULT_TOTAL_POSTS))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to connect to Reddit API");
        then(jobExecutionMetadataRepository).should().save(any(JobExecutionMetadata.class));
        then(dataExtractionProducer).should().startTextExtraction(TEST_JOB_ID, SUBREDDIT_NAME, DEFAULT_TOTAL_POSTS);
    }
}
