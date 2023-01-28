package com.skybreak.rcwa.infrastructure.persistence;

import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@EnableScan
public interface JobExecutionMetadataRepository extends CrudRepository<JobExecutionMetadata, String> {
    Optional<JobExecutionMetadata> findById(UUID id);
}