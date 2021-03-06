package com.nimbusframework.nimbuscore.wrappers.store.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DynamoRecords (
        @JsonProperty(value = "Records")
        val record: List<StoreEvent> = listOf()
)