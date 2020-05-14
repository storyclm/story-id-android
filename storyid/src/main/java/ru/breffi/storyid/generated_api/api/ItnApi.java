package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryITN;
import ru.breffi.storyid.generated_api.model.StoryITNDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ItnApi {
  /**
   * Скачивание изображения
   * Scopes: sub
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profiles/{id}/itn/download")
  Call<Void> downloadItn(
          @Path("id") String id
  );

  /**
   * Получение ИНН
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryITN&gt;
   */
  @GET("profiles/{id}/itn")
  Call<StoryITN> getItnById(
          @Path("id") String id
  );

  /**
   * Изменение ИНН
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storyITNDTO  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profiles/{id}/itn")
  Call<StoryITN> setItn(
          @Path("id") String id, @Body StoryITNDTO storyITNDTO
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
  @PUT("profiles/{id}/itn/upload")
  Call<StoryITN> uploadItn(
          @Path("id") String id, @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

  /**
   * Подтверждение ИНН
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param verified  (optional, default to false)
   * @return Call&lt;StoryITN&gt;
   */
  @PUT("profiles/{id}/itn/verify")
  Call<StoryITN> verifyItn(
          @Path("id") String id, @Query("verified") Boolean verified
  );

}
