package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditData;
import masecla.reddit4j.objects.RedditListing;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import masecla.reddit4j.objects.Time;
import masecla.reddit4j.requests.RedditCommentListingEndpointRequest;
import masecla.reddit4j.requests.SubredditPostListingEndpointRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DataExtractionProducerTest {

    private static final String SUBREDDIT_NAME = "test";
    private static final int DEFAULT_TOTAL_POSTS = 25;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Reddit4J reddit4J;

    @InjectMocks
    private DataExtractionProducer target;

    @BeforeEach
    void setup() throws AuthenticationException, IOException, InterruptedException {
        SubredditPostListingEndpointRequest postListingEndpointRequest = mock(SubredditPostListingEndpointRequest.class);
        List<RedditPost> posts = IntStream.range(0, 25)
            .boxed()
            .map(id -> {
                String text = UUID.randomUUID().toString();
                RedditPost post = new RedditPost();
                post.setId(id.toString());
                post.setTitle(text);
                post.setSelftext(text);
                return post;
            })
            .collect(Collectors.toList());

        List<RedditComment> comments = IntStream.range(0, 25)
            .boxed()
            .map(id -> {
                String text = UUID.randomUUID().toString();
                RedditComment comment = new RedditComment();
                RedditData<RedditListing<RedditData<RedditComment>>> reply = new RedditData<>();
                RedditListing<RedditData<RedditComment>> replyDataListing = new RedditListing<>();
                RedditData<RedditComment> replyRedditData = new RedditData<>();
                RedditComment replyComment = new RedditComment();
                replyRedditData.setData(replyComment);
                replyDataListing.setChildren(List.of(replyRedditData));
                reply.setData(replyDataListing);
                comment.setBody(text);
                comment.setReplies(reply);
                return comment;
            })
            .toList();

        given(postListingEndpointRequest.limit(anyInt())).willReturn(postListingEndpointRequest);
        given(postListingEndpointRequest.time(any(Time.class))).willReturn(postListingEndpointRequest);
        given(postListingEndpointRequest.submit()).willReturn(posts);
        RedditCommentListingEndpointRequest commentListingEndpointRequest = mock(RedditCommentListingEndpointRequest.class);
        given(commentListingEndpointRequest.submit()).willReturn(comments);
        willDoNothing().given(rabbitTemplate).convertAndSend(any(), any(TextPayloadEvent.class));
        given(reddit4J.getSubredditPosts(SUBREDDIT_NAME, Sorting.TOP)).willReturn(postListingEndpointRequest);
        given(reddit4J.getCommentsForPost(eq(SUBREDDIT_NAME), anyString())).willReturn(commentListingEndpointRequest);
        willDoNothing().given(reddit4J).userlessConnect();
    }

    @AfterEach
    void teardown() {
        verifyNoMoreInteractions(rabbitTemplate, reddit4J);
    }

    @Test
    void givenJobAcceptedWithSubReddit_whenClientExtractsPosts_shouldSendAllEventTypes() {
        target.startJob(SUBREDDIT_NAME, DEFAULT_TOTAL_POSTS);
        // TODO Add more assertions and verifies here
    }
}