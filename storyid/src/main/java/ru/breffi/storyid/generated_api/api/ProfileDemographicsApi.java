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

public interface ProfileDemographicsApi {
  /**
   * Получение личных данных
   * Scopes: sub
   * @return Call&lt;StoryDemographics&gt;
   */
  @GET("profile/demographics")
  Call<StoryDemographics> getDemographics();
    

  /**
   * Изменение личных данных
   * Scopes: sub
   * @param storyDemographicsDTO  (optional)
   * @return Call&lt;StoryDemographics&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/demographics")
  Call<StoryDemographics> updateDemographics(
          @Body StoryDemographicsDTO storyDemographicsDTO
  );

}
