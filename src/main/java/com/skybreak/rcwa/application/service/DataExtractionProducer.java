package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import lombok.RequiredArgsConstructor;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import masecla.reddit4j.objects.Time;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataExtractionProducer {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final Reddit4J redditClient;

    /**
     * Start the text analysis job.
     * This will scan the top <code>totalPosts</code> total posts from a particular subreddit.
     * Words will be counted across Post text and comment text.
     * This will give us an idea the most common words per each subreddit community.
     *
     * @param subreddit The subreddit we are analysing.
     * @param totalPosts The amount of top posts to scan.
     */
    public void startJob(String subreddit, int totalPosts) {
        try {
            redditClient.userlessConnect();
            List<RedditPost> posts = redditClient.getSubredditPosts(subreddit, Sorting.TOP)
                .limit(totalPosts)
                .time(Time.ALL) // TODO Make this adjustable at the API level
                .submit();
            startTextExtraction(subreddit, posts);
        } catch (AuthenticationException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to connect to Reddit API", e);
        }
    }

    private void startTextExtraction(String subreddit, List<RedditPost> posts)
            throws IOException, InterruptedException, AuthenticationException {
        for (RedditPost post : posts) {
            sendPayloadToQueue(TextPayloadEventType.POST, String.format("%s %s", post.getTitle(), post.getSelftext()));
            redditClient.getCommentsForPost(subreddit, post.getId())
                .submit()
                .stream()
                .filter(DataExtractionProducer::isValidComment)
                .forEach(comment -> {
                    sendPayloadToQueue(TextPayloadEventType.COMMENT, comment.getBody());
                    sendCommentRepliesToQueue(comment);
                });
        }
        sendPayloadToQueue(TextPayloadEventType.COMPLETION, subreddit);
    }

    private void sendCommentRepliesToQueue(RedditComment comment) {
        List<TextPayloadEvent> replies = new ArrayList<>();
        getRepliesForComment(comment, replies);
        replies.remove(0); // TODO Refactor this, comment also appears in replies
        replies.forEach(reply -> rabbitTemplate.convertAndSend(queueName, reply));
    }

    private void getRepliesForComment(RedditComment comment, List<TextPayloadEvent> replies) {
        if (isValidComment(comment)) {
            replies.add(TextPayloadEvent.builder()
                .type(TextPayloadEventType.REPLY)
                .payload(comment.getBody())
                .build());
            if (comment.getReplies() != null) {
                comment.getReplies().getData()
                    .getChildren()
                    .forEach(reply -> getRepliesForComment(reply.getData(), replies));
            }
        }
    }

    private void sendPayloadToQueue(TextPayloadEventType messageType, String data) {
        if (data != null) {
            TextPayloadEvent event = TextPayloadEvent.builder()
                .type(messageType)
                .payload(data)
                .build();
            rabbitTemplate.convertAndSend(queueName, event);
        }
    }

    private static boolean isValidComment(RedditComment comment) {
        return comment.getBody() != null && comment.getDistinguished() == null;
    }
}