package com.skybreak.rcwa.infrastructure.config;

import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public Reddit4J redditClient(@Value("${api.reddit.app_name}") String appName,
                                 @Value("${api.reddit.author}") String author,
                                 @Value("${api.reddit.version}") String version,
                                 @Value("${api.reddit.client_id}") String clientId,
                                 @Value("${api.reddit.client_secret}") String clientSecret) {
        return Reddit4J.rateLimited()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setUserAgent(new UserAgentBuilder()
                .appname(appName)
                .author(author)
                .version(version)
            );
    }
}
