package com.skybreak.rcwa.domain.model;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.junit.Rule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.dynamodb.DynaliteContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestPropertySource(locations="classpath:application-test.yaml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Testcontainers
public abstract class AbstractTestContainer {

    private static final int DYNAMO_DB_PORT = 8000;

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("amazon/dynamodb-local")
            .asCompatibleSubstituteFor("quay.io/testcontainers/dynalite");

    @Rule
    @Container
    public final DynaliteContainer dynaliteContainer = new DynaliteContainer(DOCKER_IMAGE_NAME)
            .withCommand("-jar DynamoDBLocal.jar -sharedDb")
            .withExposedPorts(DYNAMO_DB_PORT);

    /**
     * Get Dynamo client from Dynalite container.
     *
     * @return AmazonDynamoDB client
     */
    protected AmazonDynamoDB getDynamoClient() {
        return AmazonDynamoDBClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("XXX", "XXX")
                ))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:" + this.dynaliteContainer.getFirstMappedPort(),
                        Regions.EU_WEST_1.getName()
                ))
                .build();
    }
}