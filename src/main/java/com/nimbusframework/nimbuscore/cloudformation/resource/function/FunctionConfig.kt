package com.nimbusframework.nimbuscore.cloudformation.resource.function

data class FunctionConfig(val timeout: Int, val memory: Int, val stage: String)