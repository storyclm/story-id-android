package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ApplicationViewModel;
import ru.breffi.storyid.generated_api.model.CreateApplicationViewModel;
import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.ScopeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationsApi {
  /**
   * Создание нового приложения
   * Scopes:
   * @param createApplicationViewModel  (optional)
   * @return Call&lt;CreateApplicationViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("Applications")
  Call<CreateApplicationViewModel> createApplication(
          @Body CreateApplicationViewModel createApplicationViewModel
  );

  /**
   * Удаление приложения по идентификатору
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("Applications/{id}")
  Call<Void> deleteApplicationById(
          @Path("id") String id
  );

  /**
   * Получение приложения по идентификатору
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;ApplicationViewModel&gt;
   */
  @GET("Applications/{id}")
  Call<ApplicationViewModel> getApplicationById(
          @Path("id") String id
  );

  /**
   * Получение списка разрешений для сервиса
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;List&lt;ScopeViewModel&gt;&gt;
   */
  @GET("Applications/{id}/scopes")
  Call<List<ScopeViewModel>> listApplicationScopes(
          @Path("id") String id
  );

  /**
   * Список приложений
   * Scopes:
   * @return Call&lt;List&lt;ApplicationViewModel&gt;&gt;
   */
  @GET("Applications")
  Call<List<ApplicationViewModel>> listApplications();
    

  /**
   * Сбрасывает секрет
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;Void&gt;
   */
  @PUT("Applications/reset")
  Call<Void> resetApplicationSecret(
          @Query("id") String id
  );

  /**
   * Создание или изменение существующей коллекции разрешений
   * Набор актуальных разрешений. Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param requestBody  (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Applications/{id}/scopes")
  Call<List<String>> setApplicationScopes(
          @Path("id") String id, @Body List<String> requestBody
  );

  /**
   * Обновление приложения
   * Scopes:
   * @param createApplicationViewModel  (optional)
   * @return Call&lt;CreateApplicationViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Applications")
  Call<CreateApplicationViewModel> updateApplication(
          @Body CreateApplicationViewModel createApplicationViewModel
  );

}
