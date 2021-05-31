package ru.breffi.storyid.auth.common.model

class IdValueResult<T : Any> private constructor(val value: T?, val exception: Exception?) {

    companion object {

        fun ofSuccess(): IdValueResult<Unit> {
            return IdValueResult(Unit, null)
        }

        fun <T : Any> ofSuccess(value: T): IdValueResult<T> {
            return IdValueResult(value, null)
        }

        fun <T : Any> ofFailure(exception: Exception? = null): IdValueResult<T> {
            return IdValueResult(null, exception)
        }
    }

    fun success(): Boolean {
        return value != null
    }

    fun failure(): Boolean {
        return value == null
    }

    fun <R : Any> onSuccess(block: (T) -> R?): R? {
        return value?.let {
            block(it)
        }
    }

    fun <R : Any> onFailure(block: (Exception?) -> R?): R? {
        return if (value == null) {
            block(exception)
        } else {
            null
        }
    }
}

typealias IdResult = IdValueResult<Unit>