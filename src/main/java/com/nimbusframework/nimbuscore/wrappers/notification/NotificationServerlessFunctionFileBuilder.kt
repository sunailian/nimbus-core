package com.nimbusframework.nimbuscore.wrappers.notification

import com.nimbusframework.nimbuscore.annotation.annotations.function.NotificationServerlessFunction
import com.nimbusframework.nimbuscore.cloudformation.processing.MethodInformation
import com.nimbusframework.nimbuscore.persisted.NimbusState
import com.nimbusframework.nimbuscore.wrappers.ServerlessFunctionFileBuilder
import com.nimbusframework.nimbuscore.wrappers.notification.models.NotificationEvent
import com.nimbusframework.nimbuscore.wrappers.notification.models.RecordCollection
import com.nimbusframework.nimbuscore.wrappers.notification.models.SnsMessageFormat
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class NotificationServerlessFunctionFileBuilder(
        processingEnv: ProcessingEnvironment,
        methodInformation: MethodInformation,
        compilingElement: Element,
        nimbusState: NimbusState
): ServerlessFunctionFileBuilder(
        processingEnv,
        methodInformation,
        NotificationServerlessFunction::class.java.simpleName,
        NotificationEvent(),
        compilingElement,
        nimbusState
) {


    override fun writeOutput() {}

    override fun getGeneratedClassName(): String {
        return "NotificationServerlessFunction${methodInformation.className}${methodInformation.methodName}"
    }

    override fun writeImports() {
        write()

        write("import com.fasterxml.jackson.databind.ObjectMapper;")
        write("import com.amazonaws.services.lambda.runtime.Context;")
        write("import java.io.*;")
        write("import java.util.stream.Collectors;")
        if (methodInformation.packageName.isNotBlank()) {
            write("import ${methodInformation.packageName}.${methodInformation.className};")
        }
        write("import ${NotificationEvent::class.qualifiedName};")
        write("import ${RecordCollection::class.qualifiedName};")
        write("import ${SnsMessageFormat::class.qualifiedName};")

        write()
    }

    override fun writeInputs(param: Param) {

        write("RecordCollection records = objectMapper.readValue(jsonString, RecordCollection.class);")

        if (param.type != null) {
            write("NotificationEvent event = records.getRecords().get(0).getSns();")
            write("SnsMessageFormat snsFormat = objectMapper.readValue(event.getMessage(), SnsMessageFormat.class);")
            write("${param.type} parsedType;")
            write("if (snsFormat.getLambda() != null) {")
            write("parsedType = objectMapper.readValue(snsFormat.getLambda(), ${param.type}.class);")
            write("} else if (snsFormat.getDefault() != null) {")
            write("parsedType = objectMapper.readValue(snsFormat.getDefault(), ${param.type}.class);")
            write("} else {")
            write("return;")
            write("}")
        }

    }

    override fun writeFunction(inputParam: Param, eventParam: Param) {
        if (methodInformation.returnType.toString() != "void") {
            messager.printMessage(Diagnostic.Kind.WARNING, "The function ${methodInformation.className}::" +
                    "${methodInformation.methodName} has a return type which will be unused. It can be removed")
        }

        val methodName = methodInformation.methodName
        when {
            inputParam.isEmpty() && eventParam.isEmpty() -> write("handler.$methodName();")
            inputParam.type == null -> write("handler.$methodName(event);")
            eventParam.type == null -> write("handler.$methodName(parsedType);")
            inputParam.index == 0 -> write("handler.$methodName(parsedType, event);")
            else -> write("handler.$methodName(event, parsedType);")
        }
    }

    override fun writeHandleError() {
        write("e.printStackTrace();")
    }
}