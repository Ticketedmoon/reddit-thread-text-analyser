package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AnalysisManagementService {

    @Value("${api.reddit.app_name}")
    private String appName;
    @Value("${api.reddit.author}")
    private String author;
    @Value("${api.reddit.version}")
    private String version;
    @Value("${api.reddit.client_id}")
    private String clientId;
    @Value("${api.reddit.client_secret}")
    private String clientSecret;

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
            Reddit4J client = getRedditClient();
            client.userlessConnect();
            List<RedditPost> posts = client.getSubredditPosts(subreddit, Sorting.TOP)
                    .limit(totalPosts)
                    .submit();
            startTextExtraction(subreddit, client, posts);
        } catch (AuthenticationException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to connect to Reddit API", e);
        }
    }

    private void startTextExtraction(String subreddit, Reddit4J client, List<RedditPost> posts)
            throws IOException, InterruptedException, AuthenticationException {
        for (RedditPost post : posts) {
            TextPayloadEvent postTextEvent = TextPayloadEvent.builder()
                    .type(TextPayloadEventType.POST)
                    .payload(post.getTitle())
                    .build();
            client.getCommentsForPost(subreddit, post.getId())
                    .submit()
                    .forEach(comment -> {
                        TextPayloadEvent commentTextEvent = TextPayloadEvent.builder()
                                .type(TextPayloadEventType.COMMENT)
                                .payload(comment.getBody())
                                .build();
                    });
        }
    }

    private Reddit4J getRedditClient() {
        return Reddit4J.rateLimited()
                .setClientId(clientId).setClientSecret(clientSecret)
                .setUserAgent(new UserAgentBuilder()
                        .appname(appName)
                        .author(author)
                        .version(version)
                );
    }

}
