## RTTA - Reddit Thread Text Analyser

### Description
This application enables to ability to scan any subreddit of your choosing by means of counting the words used in Subreddit Posts, Comments and replies. This can be interesting to observe the most common words used by different subreddit communities and how the lingo for these communities changes over time. 
To start your own analysis, simply input a subreddit name into the input box and select the total amount of posts to analyse - maximum `100`. **Prepositions, Stop-words** and **links** will automatically be removed.

To view your jobs select the `Completed Jobs` navbar item however you will be redirected here automatically on submission of a new analysis.

#### Job Creation
![rtta_0](https://user-images.githubusercontent.com/21260839/216834186-67728efc-dcc6-4f68-a5de-58cdf3db96d2.png)

#### Completed Jobs
![rtta_1](https://user-images.githubusercontent.com/21260839/216834184-3516a6b7-d2b3-452f-be53-b15b22dd787e.png)

#### Job Result View (Overall)
![rtta_2](https://user-images.githubusercontent.com/21260839/216834182-95d9f194-e214-496a-86fd-0fe850e591d4.png)

#### Job Result View (Posts, Comments, Replies)
![rtta_3](https://user-images.githubusercontent.com/21260839/216834179-b0debf38-3ca5-4031-a106-8cebd67c962f.png)

---

### Tech
- Rabbit or some other queue tech ✅
- Kubernetes, multiple pod consumers (Probably overkill but good as a learning outcome) ❓
- AWS DynamoDB ✅ 
- Spring-boot ✅
- Helm charts (Maybe for a learning outcome) ❓
- Spring-boot actuator ✅
- Vault (Secret Management) ❓
- Test Containers ❓ (Maybe - there was some difficulty with `dynalite` containers)
- New Relic or other monitoring tools ❓
- Docker ✅
- CI/CD ✅

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
Access App via `localhost:8080`  
Access Rabbit via `localhost:15672`  
Access Dynamo via `localhost:8000`  

#### Commands:

Start RabbitMQ with Docker:
```
docker compose up
```

Remove file from remote git history:
```
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch {RELATIVE_PATH}" --prune-empty --tag-name-filter cat -- --all
```

List tables with Dynamodb CLI (local):
```
aws dynamodb list-tables --endpoint-url http://localhost:8000 --region local
```

Delete table with Dynamodb CLI (local):
```
aws dynamodb delete-table --table-name {TABLE_NAME} --endpoint-url http://localhost:8000 --region local
```

Delete table item with Dynamodb CLI (local):
```
aws dynamodb delete-item \ 
--table-name {TABLE_NAME} \
--key '{ "A": {"B": "C"} }' \
--return-values ALL_OLD \
--return-consumed-capacity TOTAL \
--return-item-collection-metrics SIZE \
--endpoint-url http://localhost:8000 \
--region local
```

Where `A` is the column key, `B` is the type ('S' = String, 'N' = Integer, ...) and `C` is the row value.  
**Note:** To delete multiple items, use the above command inside a script alternating the values in a loop. 
