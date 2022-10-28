package com.skybreak.rcwa.domain.model;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface UserThreadTextRepository extends CrudRepository<UserThreadTextItem, String> { }