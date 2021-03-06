package com.nimbusframework.nimbuscore.testing.webserver

import com.nimbusframework.nimbuscore.annotation.annotations.function.HttpMethod
import com.nimbusframework.nimbuscore.testing.http.HttpMethodIdentifier
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import com.nimbusframework.nimbuscore.testing.http.LocalHttpMethod
import com.nimbusframework.nimbuscore.testing.webserver.resources.FileResource
import com.nimbusframework.nimbuscore.testing.webserver.resources.FunctionResource
import com.nimbusframework.nimbuscore.testing.webserver.resources.WebResource
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WebserverHandler(private val indexFile: String,
                       private val errorFile: String
): AbstractHandler() {

    private val resources: MutableMap<HttpMethodIdentifier, WebResource> = mutableMapOf()
    private var errorResource: WebResource? = null

    override fun handle(
            target: String,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse
    ) {
        val httpMethod = HttpMethod.valueOf(request.method)
        var webResource: WebResource? = null
        for ((identifier, resource) in resources) {
            if (identifier.matches(target, httpMethod)) {
                webResource = resource
                break
            }
        }

        if (webResource != null) {
            webResource.writeResponse(request, response, target)
        } else {
            if (errorResource != null) {
                errorResource!!.writeResponse(request, response, target)
            } else {
                response.status = HttpServletResponse.SC_NOT_FOUND
                response.writer.close()
            }
        }
        baseRequest.isHandled = true
    }

    fun addWebResource(path: String, file: File, contentType: String) {
        addNewResource(path, HttpMethod.GET, FileResource(file, contentType))
    }

    fun addWebResource(path: String, httpMethod: HttpMethod, method: LocalHttpMethod) {
        addNewResource(path, httpMethod, FunctionResource(path, httpMethod, method))
    }

    private fun addNewResource(path: String, httpMethod: HttpMethod, webResource: WebResource) {
        val fixedPath = if (path.isNotEmpty()) "/$path" else path

        resources[HttpMethodIdentifier(fixedPath, httpMethod)] = webResource
        if (path == indexFile) {
            resources[HttpMethodIdentifier("/", HttpMethod.GET)] = webResource
        }
        if (path == errorFile) {
            errorResource = webResource
        }
    }

    fun addRedirectResource(path: String, httpMethod: HttpMethod, webResource: WebResource) {
        val fixedPath = if (path.isNotEmpty()) "/$path" else path
        resources[HttpMethodIdentifier(fixedPath, httpMethod)] = webResource
    }
}