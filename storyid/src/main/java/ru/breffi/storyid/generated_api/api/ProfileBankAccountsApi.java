package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.StoryBankAccount;
import ru.breffi.storyid.generated_api.model.StoryBankAccountDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProfileBankAccountsApi {
  /**
   * Создание банковского счета текущего пользователя
   * Scopes: sub
   * @param storyBankAccountDTO  (optional)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("profile/bankaccounts")
  Call<StoryBankAccount> createBankAccount(
          @Body StoryBankAccountDTO storyBankAccountDTO
  );

  /**
   * Удаление банковского счета текущего пользователя
   * Scopes: sub
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @DELETE("profile/bankaccounts/{id}")
  Call<StoryBankAccount> deleteBankAccountById(
          @Path("id") String id
  );

  /**
   * Получение банковского счета текущего пользователя
   * Scopes: sub
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @GET("profile/bankaccounts/{id}")
  Call<StoryBankAccount> getBankAccount(
          @Path("id") String id
  );

  /**
   * Получение списка счетов текущего пользователя
   * Scopes: sub
   * @return Call&lt;List&lt;StoryBankAccount&gt;&gt;
   */
  @GET("profile/bankaccounts")
  Call<List<StoryBankAccount>> listBankAccounts();
    

  /**
   * Изменение банковского счета текущего пользователя
   * Scopes: sub
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storyBankAccountDTO  (optional)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profile/bankaccounts/{id}")
  Call<StoryBankAccount> updateBankAccount(
          @Path("id") String id, @Body StoryBankAccountDTO storyBankAccountDTO
  );

}
