package ru.breffi.storyid.auth.common.model

data class RetryPolicy(
    val count: Int = 3,
    val delayMillis: Long = 1000,
    val codes: List<Int> = (500..599) + 402 + (405..499)
)