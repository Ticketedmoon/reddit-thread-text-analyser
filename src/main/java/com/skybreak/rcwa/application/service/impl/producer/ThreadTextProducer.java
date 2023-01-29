package com.skybreak.rcwa.application.service.impl.producer;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThreadTextProducer {

    @Value("${queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final Reddit4J redditClient;

    public void startTextExtraction(UUID jobId, String subreddit, int totalPosts) throws IOException, InterruptedException, AuthenticationException {
        redditClient.userlessConnect();
        List<RedditPost> posts = redditClient.getSubredditPosts(subreddit, Sorting.TOP)
            .limit(totalPosts)
            .time(Time.ALL) // TODO Make this adjustable at the API level
            .submit();
        for (RedditPost post : posts) {
            sendPayloadToQueue(jobId, TextPayloadEventType.POST, "%s %s".formatted(post.getTitle(), post.getSelftext()));
            sendCommentsFromPostToQueue(jobId, subreddit, post);
        }
        sendPayloadToQueue(jobId, TextPayloadEventType.COMPLETION, subreddit);
    }

    private void sendCommentsFromPostToQueue(UUID jobId, String subreddit, RedditPost post) throws IOException, InterruptedException, AuthenticationException {
        redditClient.getCommentsForPost(subreddit, post.getId())
            .submit()
            .stream()
            .filter(ThreadTextProducer::isValidComment)
            .forEach(comment -> {
                sendPayloadToQueue(jobId, TextPayloadEventType.COMMENT, comment.getBody());
                sendCommentRepliesToQueue(jobId, comment);
            });
    }

    private void sendCommentRepliesToQueue(UUID jobId, RedditComment comment) {
        List<TextPayloadEvent> replies = new ArrayList<>();
        getRepliesForComment(jobId, comment, replies);
        replies.remove(0); // TODO Refactor this, comment also appears in replies
        replies.forEach(reply -> rabbitTemplate.convertAndSend(queueName, reply));
    }

    private void getRepliesForComment(UUID jobId, RedditComment comment, List<TextPayloadEvent> replies) {
        if (isValidComment(comment)) {
            replies.add(TextPayloadEvent.builder()
                .jobId(jobId)
                .type(TextPayloadEventType.REPLY)
                .payload(comment.getBody())
                .build());
            if (comment.getReplies() != null) {
                comment.getReplies().getData()
                    .getChildren()
                    .forEach(reply -> getRepliesForComment(jobId, reply.getData(), replies));
            }
        }
    }

    private void sendPayloadToQueue(UUID jobId, TextPayloadEventType messageType, String data) {
        if (data != null) {
            TextPayloadEvent event = TextPayloadEvent.builder()
                .jobId(jobId)
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