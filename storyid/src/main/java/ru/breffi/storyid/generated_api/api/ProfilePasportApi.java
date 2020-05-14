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

public interface ProfilePasportApi {
  /**
   * Скачивание изображения страницы паспорта
   * Scopes: sub
   * @param page  (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profile/pasport/{page}/download")
  Call<Void> downloadPasportPage(
          @Path("page") Integer page
  );

  /**
   * Получение паспортных данных текущего пользователя
   * Scopes: sub
   * @return Call&lt;StoryPasport&gt;
   */
  @GET("profile/pasport")
  Call<StoryPasport> getPasport();
    

  /**
   * Удаление изображения страницы паспорта
   * Scopes: sub
   * @param page  (required)
   * @return Call&lt;StoryITN&gt;
   */
  @DELETE("profile/pasport/{page}/upload")
  Call<StoryITN> removePasportPageBlob(
          @Path("page") Integer page
  );

  /**
   * Изменение паспортных данных текущего пользователя
   * Scopes: sub
   * @param storyPasportDTO  (optional)
   * @return Call&lt;StoryPasport&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/pasport")
  Call<StoryPasport> setPasport(
          @Body StoryPasportDTO storyPasportDTO
  );

  /**
   * Загрузка изображения страницы паспорта
   * Scopes: sub
   * @param page  (required)
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;StoryITN&gt;
   */
  @Multipart
  @PUT("profile/pasport/{page}/upload")
  Call<StoryITN> uploadPasportPage(
          @Path("page") Integer page, @Part("ContentType") String contentType, @Part("ContentDisposition") String contentDisposition, @Part("Length") Long length, @Part("Name") String name, @Part("FileName") String fileName
  );

}
