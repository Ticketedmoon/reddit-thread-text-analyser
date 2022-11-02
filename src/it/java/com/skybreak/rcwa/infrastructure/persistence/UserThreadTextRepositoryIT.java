package com.skybreak.rcwa.infrastructure.persistence;

import com.skybreak.rcwa.AbstractTestContainer;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserThreadTextRepositoryIT extends AbstractTestContainer {

	@Autowired
	private UserThreadTextRepository userThreadTextRepository;

	@AfterEach
	void setup() {
		userThreadTextRepository.deleteAll();
	}

	@Test
	void givenPostWithData_whenRunFindAll_thenItemIsFound() {
		String data = "Hello, World!";
		UserThreadTextItem userThreadTextItem = UserThreadTextItem.builder()
				.type(TextPayloadEventType.POST)
				.textItem(data)
				.build();
		userThreadTextRepository.save(userThreadTextItem);
		List<UserThreadTextItem> results = (List<UserThreadTextItem>) userThreadTextRepository.findAll();

		Assertions.assertFalse(results.isEmpty());
		UserThreadTextItem item = results.get(0);
		Assertions.assertNotNull(item.getId());
		String[] uuidParts = item.getId().split("-");
		Assertions.assertEquals(5, uuidParts.length);
		Assertions.assertEquals(TextPayloadEventType.POST, item.getType());
		Assertions.assertEquals(data, item.getTextItem());
	}
}