package ru.breffi.storyid.profile.api

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.breffi.storyid.generated_api.model.StoryITN
import ru.breffi.storyid.generated_api.model.StoryPasport
import ru.breffi.storyid.generated_api.model.StorySNILS
import java.io.File
import java.util.*

interface AuxApi {

    companion object {
        fun createFilePart(file: File): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                "file",
                file.name,
                RequestBody.create(MediaType.parse(getMimeType(file)), file)
            )
        }

        private fun getMimeType(file: File): String {
            var type: String? = null
            val url = file.toString()
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.US))
            }
            if (type == null) {
                type = "image/*" // fallback type. You might set it to */*
            }
            return type
        }
    }

    @GET("profile/pasport/{page}/download/")
    fun getPassportImageAsync(@Path("page") page: Int): Call<ResponseBody>

    @Multipart
    @PUT("profile/pasport/{page}/upload")
    fun putPassportImageAsync(@Path("page") page: Int, @Part filePart: MultipartBody.Part): Call<Unit>

    @DELETE("profile/pasport/{page}/upload")
    fun deletePassportImageAsync(@Path("page") page: Int): Call<StoryPasport>


    @GET("profile/snils/download")
    fun getSnilsImageAsync(): Call<ResponseBody>

    @Multipart
    @PUT("profile/snils/upload")
    fun putSnilsImageAsync(@Part filePart: MultipartBody.Part): Call<Unit>

    @DELETE("profile/snils/upload")
    fun deleteSnilsImageAsync(): Call<StorySNILS>


    @GET("profile/itn/download")
    fun getItnImageAsync(): Call<ResponseBody>

    @Multipart
    @PUT("profile/itn/upload")
    fun putItnImageAsync(@Part filePart: MultipartBody.Part): Call<Unit>

    @DELETE("profile/itn/upload")
    fun deleteItnImageAsync(): Call<StoryITN>

    @GET("profile/files/{category}/{name}/download")
    fun getFileAsync(@Path("category") category: String, @Path("name") name: String): Call<ResponseBody>

    @Multipart
    @PUT("profile/files/{category}/{name}")
    fun putFileAsync(@Path("category") category: String, @Path("name") name: String, @Part filePart: MultipartBody.Part): Call<Unit>
}