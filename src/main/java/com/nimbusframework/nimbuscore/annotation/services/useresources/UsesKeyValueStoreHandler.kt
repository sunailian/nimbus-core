package com.nimbusframework.nimbuscore.annotation.services.useresources

import com.nimbusframework.nimbuscore.annotation.annotations.keyvalue.UsesKeyValueStore
import com.nimbusframework.nimbuscore.annotation.services.ResourceFinder
import com.nimbusframework.nimbuscore.annotation.wrappers.annotations.datamodel.UsesKeyValueStoreAnnotation
import com.nimbusframework.nimbuscore.cloudformation.CloudFormationDocuments
import com.nimbusframework.nimbuscore.cloudformation.resource.function.FunctionResource
import com.nimbusframework.nimbuscore.persisted.ClientType
import com.nimbusframework.nimbuscore.persisted.NimbusState
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class UsesKeyValueStoreHandler(
        private val cfDocuments: Map<String, CloudFormationDocuments>,
        private val processingEnv: ProcessingEnvironment,
        private val nimbusState: NimbusState
): UsesResourcesHandler {

    override fun handleUseResources(serverlessMethod: Element, functionResource: FunctionResource) {
        val iamRoleResource = functionResource.getIamRoleResource()
        val resourceFinder = ResourceFinder(cfDocuments, processingEnv, nimbusState)

        for (usesKeyValueStore in serverlessMethod.getAnnotationsByType(UsesKeyValueStore::class.java)) {
            functionResource.addClient(ClientType.KeyValueStore)

            for (stage in usesKeyValueStore.stages) {
                if (stage == functionResource.stage) {
                    val annotation = UsesKeyValueStoreAnnotation(usesKeyValueStore)
                    val resource = resourceFinder.getKeyValueStoreResource(annotation, serverlessMethod, stage)

                    if (resource != null) {
                        iamRoleResource.addAllowStatement("dynamodb:*", resource, "")
                    }
                }
            }
        }
    }
}