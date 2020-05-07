package ru.breffi.storyid.generated_api.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import ru.breffi.storyid.generated_api.model.CreateFileViewModel;
import ru.breffi.storyid.generated_api.model.FileViewModel;
import ru.breffi.storyid.generated_api.model.UpdateFileViewModel;

public interface ProfileFilesApi {
  /**
   * Создание файла 
   * Scopes: sub
   * @param createFileViewModel  (optional)
   * @return Call&lt;FileViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("profile/files")
  Call<FileViewModel> createFile(
          @retrofit2.http.Body CreateFileViewModel createFileViewModel
  );

  /**
   * Удаление файла
   * Scopes: sub
   * @param id  (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("profile/files/{id}")
  Call<Void> deleteFile(
          @retrofit2.http.Path("id") String id
  );

  /**
   * Скачивание бинарного объекта
   * Scopes: sub
   * @param category  (required)
   * @param name  (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profile/files/{category}/{name}/download")
  Call<Void> downloadCategoryFile(
          @retrofit2.http.Path("category") String category, @retrofit2.http.Path("name") String name
  );

  /**
   * Скачивание бинарного объекта
   * Scopes: sub
   * @param id  (required)
   * @return Call&lt;Void&gt;
   */
  @GET("profile/files/{id}/download")
  Call<Void> downloadFile(
          @retrofit2.http.Path("id") String id
  );

  /**
   * Получение файла в категории
   * Scopes: sub
   * @param category  (required)
   * @param name  (required)
   * @return Call&lt;FileViewModel&gt;
   */
  @GET("profile/files/{category}/{name}")
  Call<FileViewModel> getCategoryFileByName(
          @retrofit2.http.Path("category") String category, @retrofit2.http.Path("name") String name
  );

  /**
   * Получение списка файлов категории
   * Scopes: sub
   * @param category  (required)
   * @return Call&lt;List&lt;FileViewModel&gt;&gt;
   */
  @GET("profile/files/{category}")
  Call<List<FileViewModel>> listCategories(
          @retrofit2.http.Path("category") String category
  );

  /**
   * Получение списка файлов текущего пользователя
   * Scopes: sub
   * @return Call&lt;List&lt;FileViewModel&gt;&gt;
   */
  @GET("profile/files")
  Call<List<FileViewModel>> listFiles();
    

  /**
   * Редактирование метаданных
   * Scopes: sub
   * @param id  (required)
   * @param updateFileViewModel  (optional)
   * @return Call&lt;FileViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/files/{id}")
  Call<FileViewModel> updateFileMetadata(
          @retrofit2.http.Path("id") String id, @retrofit2.http.Body UpdateFileViewModel updateFileViewModel
  );

  /**
   * Загрузка и создание/обновление файла категории
   * Scopes: sub
   * @param category  (required)
   * @param name  (required)
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;FileViewModel&gt;
   */
  @retrofit2.http.Multipart
  @PUT("profile/files/{category}/{name}")
  Call<FileViewModel> uploadCategoryFile(
          @retrofit2.http.Path("category") String category, @retrofit2.http.Path("name") String name1, @retrofit2.http.Part("ContentType") String contentType, @retrofit2.http.Part("ContentDisposition") String contentDisposition, @retrofit2.http.Part("Length") Long length, @retrofit2.http.Part("Name") String name, @retrofit2.http.Part("FileName") String fileName
  );

  /**
   * Загрузка бинарного объекта
   * Scopes: sub
   * @param id  (required)
   * @param contentType  (optional)
   * @param contentDisposition  (optional)
   * @param length  (optional)
   * @param name  (optional)
   * @param fileName  (optional)
   * @return Call&lt;FileViewModel&gt;
   */
  @retrofit2.http.Multipart
  @PUT("profile/files/{id}/upload")
  Call<FileViewModel> uploadFile(
          @retrofit2.http.Path("id") String id, @retrofit2.http.Part("ContentType") String contentType, @retrofit2.http.Part("ContentDisposition") String contentDisposition, @retrofit2.http.Part("Length") Long length, @retrofit2.http.Part("Name") String name, @retrofit2.http.Part("FileName") String fileName
  );

}
