package com.skybreak.rcwa.domain.model;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.skybreak.rcwa.Application;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("local")
@Slf4j
@TestPropertySource(properties = {
		"amazon.dynamodb.endpoint=http://localhost:8000/",
		"amazon.aws.accesskey=XXX",
		"amazon.aws.secretkey=XXX" })
class UserThreadTextRepositoryIT {

	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	private UserThreadTextRepository userThreadTextRepository;

	@BeforeEach
	void setup() {
		dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

		CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(UserThreadTextItem.class);
		tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
		try {
			amazonDynamoDB.deleteTable(tableRequest.getTableName());
		} catch (ResourceNotFoundException e) {
			log.info("Dynamo table with name {} not found - recreating for {}", tableRequest.getTableName(),
					this.getClass().getSimpleName());
		}
		amazonDynamoDB.createTable(tableRequest);
		dynamoDBMapper.batchDelete(userThreadTextRepository.findAll());
	}

	@Test
	void givenPostWithData_whenRunFindAll_thenItemIsFound() {
		String id = UUID.randomUUID().toString();
		String data = "Hello, World!";
		UserThreadTextItem userThreadTextItem = UserThreadTextItem.builder()
				.id(id)
				.type(TextPayloadEventType.POST)
				.data(data)
				.build();
		userThreadTextRepository.save(userThreadTextItem);
		List<UserThreadTextItem> result = (List<UserThreadTextItem>) userThreadTextRepository.findAll();

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(id, result.get(0).getId());
		Assertions.assertEquals(TextPayloadEventType.POST, result.get(0).getType());
		Assertions.assertEquals(data, result.get(0).getData());
	}
}