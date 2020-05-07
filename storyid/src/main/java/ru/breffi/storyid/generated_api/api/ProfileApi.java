package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryProfile;
import ru.breffi.storyid.generated_api.model.StoryProfileDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProfileApi {
  /**
   * Получение профиля текущего пользователя
   * Scopes: sub
   * @return Call&lt;StoryProfile&gt;
   */
  @GET("Profile")
  Call<StoryProfile> getProfile();
    

  /**
   * Обновление текущего профиля
   * Scopes: sub
   * @param storyProfileDTO  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Profile")
  Call<StoryProfile> updateProfile(
          @Body StoryProfileDTO storyProfileDTO
  );

}
