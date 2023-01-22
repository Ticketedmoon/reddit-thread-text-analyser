package com.skybreak.rcwa.infrastructure.persistence.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.skybreak.rcwa.infrastructure.converter.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@DynamoDBTable(tableName = "job_summary_info")
public class JobExecutionMetadata {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private UUID id;

    @DynamoDBAttribute
    private String subreddit;

    @DynamoDBAttribute
    private int totalPostsToScan;

    @DynamoDBAttribute
    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    private LocalDateTime jobStartTime;

    @DynamoDBAttribute
    @DynamoDBTypeConverted( converter = LocalDateTimeConverter.class )
    private LocalDateTime jobFinishTime;

}
