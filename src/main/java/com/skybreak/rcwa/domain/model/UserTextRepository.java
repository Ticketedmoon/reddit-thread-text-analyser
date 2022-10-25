package com.skybreak.rcwa.domain.model;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface UserTextRepository extends CrudRepository<UserThreadText, String> {
    Optional<UserThreadText> findById(String id);
}