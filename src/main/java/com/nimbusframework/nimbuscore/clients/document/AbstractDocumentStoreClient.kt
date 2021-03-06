package com.nimbusframework.nimbuscore.clients.document

import com.nimbusframework.nimbuscore.annotation.annotations.document.DocumentStore
import com.nimbusframework.nimbuscore.annotation.annotations.persistent.Attribute
import com.nimbusframework.nimbuscore.annotation.annotations.persistent.Key
import com.nimbusframework.nimbuscore.clients.InvalidStageException
import java.lang.reflect.Field

abstract class AbstractDocumentStoreClient<T>(clazz: Class<T>, stage: String): DocumentStoreClient<T> {

    protected val keys: MutableMap<String, Field> = mutableMapOf()
    protected val allAttributes: MutableMap<String, Field> = mutableMapOf()
    protected val tableName: String = getTableName(clazz, stage)

    init {
        for (field in clazz.declaredFields) {
            if (field.isAnnotationPresent(Key::class.java)) {
                val keyAnnotation = field.getDeclaredAnnotation(Key::class.java)
                val columnName = if (keyAnnotation.columnName != "") keyAnnotation.columnName else field.name
                keys[columnName] = field
                allAttributes[columnName] = field
            } else if (field.isAnnotationPresent(Attribute::class.java)) {
                val attributeAnnotation = field.getDeclaredAnnotation(Attribute::class.java)
                val columnName = if (attributeAnnotation.columnName != "") attributeAnnotation.columnName else field.name
                allAttributes[columnName] = field
            }
        }
    }

    abstract override fun put(obj: T)

    abstract override fun delete(obj: T)

    abstract override fun deleteKey(keyObj: Any)

    abstract override fun getAll(): List<T>

    abstract override fun get(keyObj: Any): T?

    companion object {
        fun <T> getTableName(clazz: Class<T>, stage: String): String {
            val documentStoreAnnotations = clazz.getDeclaredAnnotationsByType(DocumentStore::class.java)
            for (documentStoreAnnotation in documentStoreAnnotations) {
                for (annotationStage in documentStoreAnnotation.stages) {
                    if (annotationStage == stage) {
                        val name = if (documentStoreAnnotation.tableName != "") documentStoreAnnotation.tableName else clazz.simpleName
                        return "$name$stage"
                    }
                }
            }
            throw InvalidStageException()
        }
    }
}