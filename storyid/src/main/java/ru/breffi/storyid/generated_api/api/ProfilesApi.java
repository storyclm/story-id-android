package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.SetPasswordViewModel;
import ru.breffi.storyid.generated_api.model.StoryProfile;
import ru.breffi.storyid.generated_api.model.StoryProfileDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProfilesApi {
  /**
   * Подтверждение адреса электронной почты
   *  Scopes:
   * @param email Адрес электронной почты (required)
   * @param confirm  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @PUT("Profiles/confirmemail")
  Call<StoryProfile> confirmEmail(
          @Query("email") String email, @Query("confirm") Boolean confirm
  );

  /**
   * Подтверждение номера мобильного телефона
   *  Scopes:
   * @param phone Номер мобильного телефона (required)
   * @param confirm  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @PUT("Profiles/confirmphone")
  Call<StoryProfile> confirmPhone(
          @Query("phone") String phone, @Query("confirm") Boolean confirm
  );

  /**
   * Создание профиля
   * Scopes:
   * @param storyProfileDTO  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("Profiles")
  Call<StoryProfile> createProfile(
          @Body StoryProfileDTO storyProfileDTO
  );

  /**
   * Удаление профиля
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryProfile&gt;
   */
  @DELETE("Profiles/{id}")
  Call<StoryProfile> deleteProfileById(
          @Path("id") String id
  );

  /**
   * Получение профиля по идентификатору
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryProfile&gt;
   */
  @GET("Profiles/{id}")
  Call<StoryProfile> getProfileById(
          @Path("id") String id
  );

  /**
   * Профили по списку идентификаторов
   *  Scopes:
   * @param ids  (optional, default to new ArrayList&lt;String&gt;())
   * @return Call&lt;List&lt;StoryProfile&gt;&gt;
   */
  @GET("Profiles/package")
  Call<List<StoryProfile>> getProfilesByIds(
          @Query("ids") List<String> ids
  );

  /**
   * Список профилей
   *  Scopes:
   * @return Call&lt;List&lt;StoryProfile&gt;&gt;
   */
  @GET("Profiles")
  Call<List<StoryProfile>> listProfiles();
    

  /**
   * Получение пользователя по login
   * Данный запрос принимает login, под логином понимается одно из следуюзих полей: phone, email или username
   * @param login  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @GET("Profiles/exists")
  Call<StoryProfile> profileExists(
          @Query("login") String login
  );

  /**
   * Присвоение нового пароля
   *  Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param setPasswordViewModel  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Profiles/{id}/setpassword")
  Call<Void> setPassword(
          @Path("id") String id, @Body SetPasswordViewModel setPasswordViewModel
  );

  /**
   * Обновление профиля
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storyProfileDTO  (optional)
   * @return Call&lt;StoryProfile&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Profiles/{id}")
  Call<StoryProfile> updateProfile(
          @Path("id") String id, @Body StoryProfileDTO storyProfileDTO
  );

}
