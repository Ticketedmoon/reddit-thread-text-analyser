package com.skybreak.rcwa.domain.model;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.dynamodb.DynaliteContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestPropertySource(locations="classpath:application-test.yaml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Testcontainers
public abstract class AbstractTestContainer {

    private static final int DYNAMO_DB_PORT = 8000;

    private static final DockerImageName dockerImageName = DockerImageName.parse("amazon/dynamodb-local")
            .asCompatibleSubstituteFor("quay.io/testcontainers/dynalite");

    @Container
    protected final DynaliteContainer dynamoDbContainer = new DynaliteContainer(dockerImageName)
            .withExposedPorts(DYNAMO_DB_PORT)
            .withNetwork(Network.SHARED);
}