package ru.breffi.storyidsample.api.base

import kotlin.annotation.Target
import kotlin.annotation.Retention

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ExcludeSerialization