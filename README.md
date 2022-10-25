## RCWA - Reddit Community Word Analyser

### Description
Track the most common words in particular Subreddits.
 - Initially start with the ability to hook into 1 subreddit.
 - Analyse all posts and comments from today back to a particular time period.

### Tech
- Rabbit or some other queue tech. ✅
- Kubernetes, multiple consumers (pods).
- AWS DynamoDB 
- Spring-boot ✅
- Helm charts (Maybe)
- Spring-boot actuator
- Vault (Secret Management)
- New Relic or other monitoring tools.
- Docker.
- AWS (S3/EC2/ELB)

### Arch:

Producer:
- Hit Reddit API for particular subreddit posts over a particular time period.
- For each Post, extract post data and send Event with type: POST.
- For each Comment, extract comment data and send Event with type: COMMENT.  
**Note:** Investigate RateLimiting here, use headers to deduce amount of requests left.  
- When finished, emit Event with type: COMPLETION_EVENT

Consumer(s):
- Extract data from payload of event
- Split post based on spaces
- Increment each word's count in DynamoDB.
**Note:** Ensure operations are atomic/no sync issues across pods.  
**Note:** Removal of misspelled words and prepositions is advised.

### Local Development
Access via `localhost:15672`

#### Commands:

Start RabbitMQ with Docker:
```
docker compose up
```