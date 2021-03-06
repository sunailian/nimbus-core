package com.nimbusframework.nimbuscore.wrappers.basic

import com.nimbusframework.nimbuscore.annotation.annotations.function.BasicServerlessFunction
import com.nimbusframework.nimbuscore.cloudformation.processing.MethodInformation
import com.nimbusframework.nimbuscore.persisted.NimbusState
import com.nimbusframework.nimbuscore.wrappers.ServerlessFunctionFileBuilder
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class BasicServerlessFunctionFileBuilder(
        processingEnv: ProcessingEnvironment,
        methodInformation: MethodInformation,
        compilingElement: Element,
        nimbusState: NimbusState
) : ServerlessFunctionFileBuilder(
        processingEnv,
        methodInformation,
        BasicServerlessFunction::class.java.simpleName,
        null,
        compilingElement,
        nimbusState
) {
    override fun getGeneratedClassName(): String {
        return "BasicServerlessFunction${methodInformation.className}${methodInformation.methodName}"
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

        write()
    }

    override fun writeInputs(param: Param) {

        if (param.type != null) {
            write("${param.type} parsedType;")
            write("try {")
            write("parsedType = objectMapper.readValue(jsonString, ${param.type}.class);")
            write("} catch (Exception e) {")
            write("e.printStackTrace();")
            write("output.close();")
            write("return;")
            write("}")
        }

    }

    override fun writeFunction(inputParam: Param, eventParam: Param) {
        val callPrefix = if (methodInformation.returnType.toString() == "void") {
            ""
        } else {
            "${methodInformation.returnType} result = "
        }

        val methodName = methodInformation.methodName
        when {
            inputParam.isEmpty() && eventParam.isEmpty() -> write("${callPrefix}handler.$methodName();")
            else -> write("${callPrefix}handler.$methodName(parsedType);")
        }
    }

    override fun writeOutput() {
        if (methodInformation.returnType.toString() != "void") {
            write("String resultString = objectMapper.writeValueAsString(result);")
            write("PrintWriter writer = new PrintWriter(output);")
            write("writer.print(resultString);")
            write("writer.close();")
        }
        write("output.close();")
    }

    override fun writeHandleError() {
        write("e.printStackTrace();")
    }

    override fun isValidFunction(functionParams: FunctionParams) {
        if (methodInformation.parameters.size > 1) {
            compilationError("Too many parameters for BasicServerlessFunction, maximum 1 of type T")
        }
    }
}