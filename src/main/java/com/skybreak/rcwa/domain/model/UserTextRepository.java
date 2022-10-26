package com.skybreak.rcwa.domain.model;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface UserTextRepository extends CrudRepository<UserThreadTextItem, String> {
    Optional<UserThreadTextItem> findById(String id);
}