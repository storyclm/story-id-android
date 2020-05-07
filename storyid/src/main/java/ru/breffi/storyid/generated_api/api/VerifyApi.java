package ru.breffi.storyid.generated_api.api;

import ru.breffi.storyid.generated_api.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import ru.breffi.storyid.generated_api.model.CodeVerifyViewModel;
import ru.breffi.storyid.generated_api.model.LoginVerifyRequestViewModel;
import ru.breffi.storyid.generated_api.model.ProblemDetails;
import ru.breffi.storyid.generated_api.model.UserExistsRequestViewModel;
import ru.breffi.storyid.generated_api.model.VerifyPhoneResultViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface VerifyApi {
  /**
   * Проверка на существования пользователя
   * Данный запрос принимает login, под логином понимается одно из следуюзих полей: phone, email или username
   * @param userExistsRequestViewModel  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Verify/exists")
  Call<Void> loginExists(
          @Body UserExistsRequestViewModel userExistsRequestViewModel
  );

  /**
   * Верификация номера мобильного телефона
   * 
   * @param codeVerifyViewModel  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("Verify/phone")
  Call<Void> sendPhoneVerificationCode(
          @Body CodeVerifyViewModel codeVerifyViewModel
  );

  /**
   * Получения верификационного кода
   * Данный запрос создает код на верификацию и отправляет по СМС или email каналу в зависимости от типа логина
   * @param loginVerifyRequestViewModel  (optional)
   * @return Call&lt;VerifyPhoneResultViewModel&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("Verify/code")
  Call<VerifyPhoneResultViewModel> sendVerificationCode(
          @Body LoginVerifyRequestViewModel loginVerifyRequestViewModel
  );

}
