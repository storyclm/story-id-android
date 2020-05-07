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

public interface ProfileItnApi {
  /**
   * Получение изображения ИНН текущего пользователя
   * Scopes: sub
   * @return Call&lt;Void&gt;
   */
  @GET("profile/itn/download")
  Call<Void> downloadItn();
    

  /**
   * ИНН текущего пользователя
   * Scopes:
   * @return Call&lt;StoryITN&gt;
   */
  @GET("profile/itn")
  Call<StoryITN> getItn();
    

  /**
   * Удаление изображения ИНН текущего пользователя
   * Scopes: sub
   * @return Call&lt;StoryITN&gt;
   */
  @DELETE("profile/itn/upload")
  Call<StoryITN> removeItnBlob();
    

  /**
   * Изменение ИНН текущего пользователя
   * Scopes:
   * @param storyITNDTO  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/itn")
  Call<StoryITN> setItn(
          @Body StoryITNDTO storyITNDTO
  );

  /**
   * Загрузка изображения ИНН текущего пользователя
   * Scopes: sub
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Multipart
  @PUT("profile/itn/upload")
  Call<StoryITN> uploadItn(
          @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

}
