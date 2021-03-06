package com.nimbusframework.nimbuscore.testing.services.function

import com.nimbusframework.nimbuscore.annotation.annotations.function.HttpMethod
import com.nimbusframework.nimbuscore.annotation.annotations.function.HttpServerlessFunction
import com.nimbusframework.nimbuscore.testing.LocalNimbusDeployment.Companion.functionWebserverIdentifier
import com.nimbusframework.nimbuscore.testing.function.FunctionIdentifier
import com.nimbusframework.nimbuscore.testing.http.HttpMethodIdentifier
import com.nimbusframework.nimbuscore.testing.http.LocalHttpMethod
import com.nimbusframework.nimbuscore.testing.services.LocalResourceHolder
import com.nimbusframework.nimbuscore.testing.webserver.WebserverHandler
import java.lang.reflect.Method

class LocalHttpFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val httpPort: Int,
        private val variableSubstitution: MutableMap<String, String>,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method) {
        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        val httpServerlessFunctions = method.getAnnotationsByType(HttpServerlessFunction::class.java)

        for (httpFunction in httpServerlessFunctions) {
            if (httpFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val httpMethod = LocalHttpMethod(method, invokeOn)
                if (httpFunction.method != HttpMethod.ANY) {
                    val httpIdentifier = HttpMethodIdentifier(httpFunction.path, httpFunction.method)
                    localResourceHolder.httpMethods[httpIdentifier] = httpMethod

                } else {
                    for (httpMethodType in HttpMethod.values()) {
                        val httpIdentifier = HttpMethodIdentifier(httpFunction.path, httpMethodType)
                        localResourceHolder.httpMethods[httpIdentifier] = httpMethod
                    }
                }
                localResourceHolder.methods[functionIdentifier] = httpMethod

                val lambdaWebserver = localResourceHolder.webservers.getOrPut(functionWebserverIdentifier) {
                    variableSubstitution["\${NIMBUS_REST_API_URL}"] = "http://localhost:$httpPort/$functionWebserverIdentifier"
                    WebserverHandler("", "")
                }

                lambdaWebserver.addWebResource(httpFunction.path, httpFunction.method, httpMethod)
            }
        }
    }
}