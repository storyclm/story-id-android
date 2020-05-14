package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.ScopeViewModel;
import ru.breffi.storyid.generated_api.model.ServiceViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ServicesApi {
  /**
   * Создание нового сервиса
   * Scopes:
   * @param serviceViewModel  (optional)
   * @return Call&lt;ServiceViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("Services")
  Call<ServiceViewModel> createService(
          @Body ServiceViewModel serviceViewModel
  );

  /**
   * Удаление сервиса по идентификатору
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("Services/{id}")
  Call<Void> deleteServiceById(
          @Path("id") String id
  );

  /**
   * Получение сервиса по идентификатору
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;ServiceViewModel&gt;
   */
  @GET("Services/{id}")
  Call<ServiceViewModel> getServiceById(
          @Path("id") String id
  );

  /**
   * Получение списка разрешений для сервиса
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;List&lt;ScopeViewModel&gt;&gt;
   */
  @GET("Services/{id}/scopes")
  Call<List<ScopeViewModel>> listServiceScopes(
          @Path("id") String id
  );

  /**
   * Список сервисов
   * Scopes:
   * @return Call&lt;List&lt;ServiceViewModel&gt;&gt;
   */
  @GET("Services")
  Call<List<ServiceViewModel>> listServices();
    

  /**
   * Удаление разрешения
   * Scopes:
   * @param id Идентификатор сервиса (required)
   * @param name Разрешение (required)
   * @return Call&lt;List&lt;ScopeViewModel&gt;&gt;
   */
  @DELETE("Services/{id}/scopes")
  Call<List<ScopeViewModel>> removeServiceScope(
          @Path("id") String id, @Query("name") String name
  );

  /**
   * Создание или изменения существующегоразрешения
   * Если разрешение существует, то оно будет изменено иначе оно будет создано. Уникальным идентификатором в данной операции является поле \&quot;Name\&quot;, по нему происходит проверка на сущестование разрешения. Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param scopeViewModel  (optional)
   * @return Call&lt;List&lt;ScopeViewModel&gt;&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Services/{id}/scopes")
  Call<List<ScopeViewModel>> setServiceScope(
          @Path("id") String id, @Body ScopeViewModel scopeViewModel
  );

  /**
   * Обновление сервиса
   * Scopes:
   * @param serviceViewModel  (optional)
   * @return Call&lt;ServiceViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Services")
  Call<ServiceViewModel> updateService(
          @Body ServiceViewModel serviceViewModel
  );

}
