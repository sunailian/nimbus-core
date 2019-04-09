package com.nimbusframework.nimbuscore.testing.services.usesresources

import com.nimbusframework.nimbuscore.annotation.annotations.websocket.UsesServerlessFunctionWebSocketClient
import com.nimbusframework.nimbuscore.testing.function.FunctionEnvironment
import com.nimbusframework.nimbuscore.testing.function.PermissionType
import com.nimbusframework.nimbuscore.testing.function.permissions.AlwaysTruePermission
import com.nimbusframework.nimbuscore.testing.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesWebSocketHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesWebSocketManagers = method.getAnnotationsByType(UsesServerlessFunctionWebSocketClient::class.java)

        for (usesWebSocketManager in usesWebSocketManagers) {
            if (usesWebSocketManager.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.WEBSOCKET_MANAGER, AlwaysTruePermission())
            }
        }
    }

}