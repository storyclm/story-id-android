package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryITN;
import ru.breffi.storyid.generated_api.model.StorySNILS;
import ru.breffi.storyid.generated_api.model.StorySNILSDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SnilsApi {
  /**
   * Скачивание изображения
   * Scopes: sub
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profiles/{id}/snils/download")
  Call<Void> downloadSnils(
          @Path("id") String id
  );

  /**
   * Получение СНИЛС
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StorySNILS&gt;
   */
  @GET("profiles/{id}/snils")
  Call<StorySNILS> getSnilsById(
          @Path("id") String id
  );

  /**
   * Изменение СНИЛС
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storySNILSDTO  (optional)
   * @return Call&lt;StorySNILS&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profiles/{id}/snils")
  Call<StorySNILS> setSnils(
          @Path("id") String id, @Body StorySNILSDTO storySNILSDTO
  );

  /**
   * Загрузка изображения
   * Scopes: sub
   * @param id  (required)
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Multipart
  @PUT("profiles/{id}/snils/upload")
  Call<StoryITN> uploadSnils(
          @Path("id") String id, @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

  /**
   * Подтверждение ИНН
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param verified  (optional, default to false)
   * @return Call&lt;StorySNILS&gt;
   */
  @PUT("profiles/{id}/snils/verify")
  Call<StorySNILS> verifySnils(
          @Path("id") String id, @Query("verified") Boolean verified
  );

}
