package com.nimbusframework.nimbuscore.cloudformation.resource.websocket

import com.nimbusframework.nimbuscore.cloudformation.resource.Resource
import com.google.gson.JsonObject
import com.nimbusframework.nimbuscore.persisted.NimbusState
import java.util.*

class WebSocketDeployment(
        private val webSocketApi: WebSocketApi,
        nimbusState: NimbusState
): Resource(nimbusState, webSocketApi.stage) {

    private val creationTime = Calendar.getInstance().timeInMillis

    override fun toCloudFormation(): JsonObject {
        val deployment = JsonObject()
        deployment.addProperty("Type", "AWS::ApiGatewayV2::Deployment")

        val properties = getProperties()
        properties.add("ApiId", webSocketApi.getRef())

        deployment.add("Properties", properties)

        deployment.add("DependsOn", dependsOn)
        return deployment
    }

    override fun getName(): String {
        return "WebsocketApiDeployment$creationTime"
    }
}