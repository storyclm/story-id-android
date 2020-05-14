package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryDemographics;
import ru.breffi.storyid.generated_api.model.StoryDemographicsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DemographicsApi {
  /**
   * Получение личных данных
   * Scopes:
   * @param id Уникальный идентификатор профиля в формате StoryId (required)
   * @return Call&lt;StoryDemographics&gt;
   */
  @GET("profiles/{id}/demographics")
  Call<StoryDemographics> getDemographicsById(
          @Path("id") String id
  );

  /**
   * Изменение личных данных
   * Scopes:
   * @param id Уникальный идентификатор профиля в формате StoryId (required)
   * @param storyDemographicsDTO  (optional)
   * @return Call&lt;StoryDemographics&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profiles/{id}/demographics")
  Call<StoryDemographics> setDemographics(
          @Path("id") String id, @Body StoryDemographicsDTO storyDemographicsDTO
  );

  /**
   * Подтверждение личных данных
   * Scopes:
   * @param id Уникальный идентификатор профиля в формате StoryId (required)
   * @param verified  (optional, default to false)
   * @return Call&lt;StoryDemographics&gt;
   */
  @PUT("profiles/{id}/demographics/verify")
  Call<StoryDemographics> verifyDemographics(
          @Path("id") String id, @Query("verified") Boolean verified
  );

}
