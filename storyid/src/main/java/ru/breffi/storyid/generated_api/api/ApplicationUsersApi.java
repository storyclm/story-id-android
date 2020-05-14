package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.AppUserDTO;
import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryAppUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationUsersApi {
  /**
   * Получение списка пользователя приложения
   * 
   * @param сlient  (required)
   * @param client Название клиента (required)
   * @return Call&lt;List&lt;StoryAppUser&gt;&gt;
   */
  @GET("ApplicationUsers/{сlient}")
  Call<List<StoryAppUser>> listApplicationUsers(
          @Path("сlient") String сlient, @Query("client") String client
  );

  /**
   * Добавление пользоваетля в приложение через клиент
   * 
   * @param client Идентификатор клиента (required)
   * @param appUserDTO  (optional)
   * @return Call&lt;StoryAppUser&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("ApplicationUsers/{client}")
  Call<StoryAppUser> setApplicationUsers(
          @Path("client") String client, @Body AppUserDTO appUserDTO
  );

}
