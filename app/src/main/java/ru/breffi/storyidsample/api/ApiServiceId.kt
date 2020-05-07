package ru.breffi.storyidsample.api

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import ru.breffi.storyidsample.BuildConfig
import ru.breffi.storyidsample.valueobject.ServerImageFile

interface ApiServiceId {

    companion object {
        const val BASE_URL = BuildConfig.API_ID_ENDPOINT
    }

    @GET("profile/files/storyidsample/avatar/download")
    fun getAvatarImageAsync(): Deferred<ResponseBody>

    @Multipart
    @PUT("profile/files/storyidsample/avatar")
    fun putAvatarImageAsync(@Part filePart: MultipartBody.Part): Deferred<Unit>

    @GET("profile/files/storyidsample/avatar")
    fun getAvatarIdAsync(): Deferred<ServerImageFile>

    @DELETE("profile/files/{id}")
    fun deleteAvatarImageAsync(@Path("id") id: String): Deferred<Unit>
}