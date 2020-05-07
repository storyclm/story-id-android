package ru.breffi.storyidsample.api.base

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class ExcludeDeserializationStrategy : ExclusionStrategy {

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(ExcludeDeserialization::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }

}