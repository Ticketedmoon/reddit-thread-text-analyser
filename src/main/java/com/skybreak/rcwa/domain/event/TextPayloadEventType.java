package com.skybreak.rcwa.domain.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public enum TextPayloadEventType {
    POST,
    COMMENT,
    REPLY,
    COMPLETION
}
