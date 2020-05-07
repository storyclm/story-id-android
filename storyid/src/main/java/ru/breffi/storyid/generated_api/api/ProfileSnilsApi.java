package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryITN;
import ru.breffi.storyid.generated_api.model.StorySNILS;
import ru.breffi.storyid.generated_api.model.StorySNILSDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProfileSnilsApi {
  /**
   * Скачивание изображения СНИЛС текущего пользователя
   * Scopes: sub
   * @return Call&lt;Void&gt;
   */
  @GET("profile/snils/download")
  Call<Void> downloadSnils();
    

  /**
   * Получение СНИЛС текущего пользователя
   * Scopes: sub
   * @return Call&lt;StorySNILS&gt;
   */
  @GET("profile/snils")
  Call<StorySNILS> getSnils();
    

  /**
   * Удаление изображения СНИЛС текущего пользователя
   * Scopes: sub
   * @return Call&lt;StoryITN&gt;
   */
  @DELETE("profile/snils/upload")
  Call<StoryITN> removeSnilsBlob();
    

  /**
   * Изменение СНИЛС текущего пользователя
   * Scopes: sub
   * @param storySNILSDTO  (optional)
   * @return Call&lt;StorySNILS&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/snils")
  Call<StorySNILS> setSnils(
          @Body StorySNILSDTO storySNILSDTO
  );

  /**
   * Загрузка изображения СНИЛС текущего пользователя
   * Scopes: sub
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Multipart
  @PUT("profile/snils/upload")
  Call<StoryITN> uploadSnils(
          @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

}
