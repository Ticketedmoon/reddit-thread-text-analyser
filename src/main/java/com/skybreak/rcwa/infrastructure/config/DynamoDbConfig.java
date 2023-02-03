package com.skybreak.rcwa.infrastructure.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.amazonaws.regions.Regions.EU_WEST_1;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.skybreak.rcwa.infrastructure.persistence")
@Slf4j
public class DynamoDbConfig {

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSCredentials awsCredentials) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, EU_WEST_1.getName()))
                .build();

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
        init(dynamoDBMapper, client);
        return client;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }

    private void init(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB client) {
        createTableIfNotExists(dynamoDBMapper, client, UserThreadTextItem.class);
        createTableIfNotExists(dynamoDBMapper, client, JobExecutionMetadata.class);
    }

    private void createTableIfNotExists(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB client, Class<?> dynamoRepositoryClass) {
        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(dynamoRepositoryClass);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        boolean isTableCreated = TableUtils.createTableIfNotExists(client, tableRequest);
        if (isTableCreated) {
            log.info("Table for DAO [{}] has been created", dynamoRepositoryClass.getName());
        }
    }
}
