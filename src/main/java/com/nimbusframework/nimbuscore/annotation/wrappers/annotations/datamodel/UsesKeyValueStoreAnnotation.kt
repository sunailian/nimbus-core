package com.nimbusframework.nimbuscore.annotation.wrappers.annotations.datamodel

import com.nimbusframework.nimbuscore.annotation.annotations.keyvalue.UsesKeyValueStore

class UsesKeyValueStoreAnnotation(private val usesKeyValueStoreAnnotation: UsesKeyValueStore): DataModelAnnotation() {

    override val stages = usesKeyValueStoreAnnotation.stages

    override fun internalDataModel(): Class<out Any> {
        return usesKeyValueStoreAnnotation.dataModel.java
    }

}