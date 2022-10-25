package com.skybreak.rcwa;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.domain.model.UserTextRepository;
import com.skybreak.rcwa.domain.model.UserThreadText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = {
		"amazon.dynamodb.endpoint=http://localhost:8000/",
		"amazon.aws.accesskey=XXX",
		"amazon.aws.secretkey=XXX" })
class UserTextRepositoryIT {

	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	private UserTextRepository repository;

	@BeforeEach
	void setup() {
		dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

		CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(UserThreadText.class);
		tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
		amazonDynamoDB.createTable(tableRequest);
		dynamoDBMapper.batchDelete(repository.findAll());
	}

	@Test
	void givenPostWithData_whenRunFindAll_thenItemIsFound() {
		UserThreadText userThreadText = UserThreadText.builder()
				.type(TextPayloadEventType.POST)
				.data("XXX") // TODO - fix me
				.build();
		repository.save(userThreadText);
		List<UserThreadText> result = (List<UserThreadText>) repository.findAll();

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("XXX", result.get(0).getData());
	}
}