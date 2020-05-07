package ru.breffi.storyidsample.ui.confirm_code

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.auth.common.model.AuthError
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyid.auth.common.model.AuthSuccess
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.repository.EncryptSharedPreferences
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject

class ConfirmCodeViewModel @Inject
constructor(
    private val preferencesRepository: EncryptSharedPreferences,
    private val appAuthRepository: AppAuthRepository
) : ViewModel() {

    val confirmResponse = MutableLiveData<Resource<AuthState>>()
    val resendResponse = MutableLiveData<Resource<AuthState>>()

    fun confirm(code: String) {
        viewModelScope.launch {
            confirmResponse.postValue(Resource.loading(null))
            try {
                when (val authState = appAuthRepository.confirmCode(code)) {
                    AuthSuccess -> confirmResponse.postValue(Resource.success(authState))
                    is AuthError -> confirmResponse.postValue(Resource.error(authState.error, authState))
                }
                preferencesRepository.resetPin() //После успешной авторизации сбрасываем пин-код
            } catch (t: Throwable) {
                confirmResponse.postValue(Resource.error(t, null))
            }
        }
    }

    fun resendCode(phone: String) {
        viewModelScope.launch {
            try {
                when (val authState = appAuthRepository.login(phone)) {
                    AuthSuccess -> resendResponse.postValue(Resource.success(authState))
                    is AuthError -> resendResponse.postValue(Resource.error(authState.error, authState))
                }
            } catch (t: Throwable) {
                resendResponse.postValue(Resource.error(t, null))
            }
        }
    }

}