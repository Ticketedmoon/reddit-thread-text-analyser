package com.skybreak.rcwa.infrastructure.persistence;

import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface UserThreadTextRepository extends CrudRepository<UserThreadTextItem, String> {
    UserThreadTextItem findByTypeAndTextItem(TextPayloadEventType type, String textItem);
}