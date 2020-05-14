package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryITN;
import ru.breffi.storyid.generated_api.model.StoryPasport;
import ru.breffi.storyid.generated_api.model.StoryPasportDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PasportApi {
  /**
   * Скачивание изображения
   * Scopes: sub
   * @param id  (required)
   * @param page  (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profiles/{id}/pasport/{page}/download")
  Call<Void> downloadPasportPage(
          @Path("id") String id, @Path("page") Integer page
  );

  /**
   * Получение паспортных данных
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryPasport&gt;
   */
  @GET("profiles/{id}/pasport")
  Call<StoryPasport> getPasportById(
          @Path("id") String id
  );

  /**
   * Изменение паспортных данных
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storyPasportDTO  (optional)
   * @return Call&lt;StoryPasport&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profiles/{id}/pasport")
  Call<StoryPasport> setPasport(
          @Path("id") String id, @Body StoryPasportDTO storyPasportDTO
  );

  /**
   * Загрузка изображения
   * Scopes: sub
   * @param id  (required)
   * @param page  (required)
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Multipart
  @PUT("profiles/{id}/pasport/{page}/upload")
  Call<StoryITN> uploadPasportPage(
          @Path("id") String id, @Path("page") Integer page, @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

  /**
   * Подтверждение паспортных данных
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param verified  (optional, default to false)
   * @return Call&lt;StoryPasport&gt;
   */
  @PUT("profiles/{id}/pasport/verify")
  Call<StoryPasport> verifyPasport(
          @Path("id") String id, @Query("verified") Boolean verified
  );

}
