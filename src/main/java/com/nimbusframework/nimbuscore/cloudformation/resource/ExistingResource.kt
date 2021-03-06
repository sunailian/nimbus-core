package com.nimbusframework.nimbuscore.cloudformation.resource

import com.nimbusframework.nimbuscore.persisted.NimbusState
import com.google.gson.JsonObject

class ExistingResource(
        private val arn: String,
        nimbusState: NimbusState,
        stage: String
) : Resource(nimbusState, stage) {
    override fun toCloudFormation(): JsonObject {
        return JsonObject()
    }

    override fun getName(): String {
        return ""
    }

    override fun getArn(suffix: String): JsonObject {
        val arnJson = JsonObject()
        arnJson.addProperty("Arn", arn)
        return arnJson
    }
}
