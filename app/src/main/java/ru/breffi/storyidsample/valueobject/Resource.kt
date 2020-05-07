package ru.breffi.storyidsample.valueobject

data class Resource<T>(
    val status: Status,
    val data: T?,
    val error: Throwable?
) {

    val isSucceed: Boolean = status == Status.SUCCESS
    val isLoading: Boolean = status == Status.LOADING
    val isError: Boolean = status == Status.ERROR

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, error)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

}