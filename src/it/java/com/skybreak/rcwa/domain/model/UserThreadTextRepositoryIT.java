package com.skybreak.rcwa.domain.model;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserThreadTextRepositoryIT extends AbstractTestContainer {

	private static final String TEST_TABLE_SUFFIX = "_test";

	@Autowired
	private UserThreadTextRepository userThreadTextRepository;

	@BeforeEach
	void setup() {
		AmazonDynamoDB client = getDynamoClient();
		DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
		CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(UserThreadTextItem.class);
		tableRequest.setTableName(tableRequest.getTableName() + TEST_TABLE_SUFFIX);
		tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
		try {
			client.deleteTable(tableRequest.getTableName());
		} catch (ResourceNotFoundException e) {
			log.info("Dynamo table with name {} not found - recreating for {}", tableRequest.getTableName(), getClass().getSimpleName());
		}
		client.createTable(tableRequest);
		Iterable<UserThreadTextItem> allRecords = userThreadTextRepository.findAll();
		dynamoDBMapper.batchDelete(allRecords);
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