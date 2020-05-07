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
import ru.breffi.storyid.generated_api.model.StoryITN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BankAccountsApi {
  /**
   * Создание банковского счета
   * Scopes:
   * @param profileId Уникальный идентификатор в формате StoryId (required)
   * @param storyBankAccountDTO  (optional)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("profiles/{profileId}/bankaccounts")
  Call<StoryBankAccount> createBankAccount(
          @Path("profileId") String profileId, @Body StoryBankAccountDTO storyBankAccountDTO
  );

  /**
   * Удаление банковского счета
   * Scopes:
   * @param profileId Уникальный идентификатор в формате StoryId (required)
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @DELETE("profiles/{profileId}/bankaccounts/{id}")
  Call<StoryBankAccount> deleteBankAccountById(
          @Path("profileId") String profileId, @Path("id") String id
  );

  /**
   * Получение банковского счета
   * Scopes:
   * @param profileId Уникальный идентификатор в формате StoryId (required)
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @GET("profiles/{profileId}/bankaccounts/{id}")
  Call<StoryBankAccount> getBankAccountById(
          @Path("profileId") String profileId, @Path("id") String id
  );

  /**
   * Получение банковского счета
   * Scopes:
   * @param profileId Уникальный идентификатор в формате StoryId (required)
   * @return Call&lt;List&lt;StoryBankAccount&gt;&gt;
   */
  @GET("profiles/{profileId}/bankaccounts")
  Call<List<StoryBankAccount>> listBankAccounts(
          @Path("profileId") String profileId
  );

  /**
   * Изменение банковского счета
   * Scopes:
   * @param profileId Уникальный идентификатор в формате StoryId (required)
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param storyBankAccountDTO  (optional)
   * @return Call&lt;StoryBankAccount&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("profiles/{profileId}/bankaccounts/{id}")
  Call<StoryBankAccount> updateBankAccount(
          @Path("profileId") String profileId, @Path("id") String id, @Body StoryBankAccountDTO storyBankAccountDTO
  );

  /**
   * Подтверждение банковского счета
   * Scopes:
   * @param id Уникальный идентификатор в формате StoryId (required)
   * @param profileId  (required)
   * @param verified  (optional, default to false)
   * @return Call&lt;StoryITN&gt;
   */
  @PUT("profiles/{profileId}/bankaccounts/{id}/verify")
  Call<StoryITN> verifyBankAccount(
          @Path("id") String id, @Path("profileId") String profileId, @Query("verified") Boolean verified
  );

}
