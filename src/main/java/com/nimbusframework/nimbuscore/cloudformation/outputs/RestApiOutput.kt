package com.nimbusframework.nimbuscore.cloudformation.outputs

import com.nimbusframework.nimbuscore.cloudformation.resource.http.RestApi
import com.nimbusframework.nimbuscore.configuration.REST_API_URL_OUTPUT
import com.nimbusframework.nimbuscore.persisted.NimbusState

class RestApiOutput(
        restApi: RestApi,
        nimbusState: NimbusState
): ApiGatewayOutput(
        restApi,
        nimbusState,
        "https://"
) {
    override fun getExportName(): String {
        return "${nimbusState.projectName}-$stage-$REST_API_URL_OUTPUT"
    }
}