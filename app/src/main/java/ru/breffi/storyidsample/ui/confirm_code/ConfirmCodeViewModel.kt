package ru.breffi.storyidsample.ui.confirm_code

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.auth.common.model.IdResult
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.repository.EncryptSharedPreferences
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject

class ConfirmCodeViewModel @Inject
constructor(
    private val preferencesRepository: EncryptSharedPreferences,
    private val appAuthRepository: AppAuthRepository
) : ViewModel() {

    val confirmResponse = MutableLiveData<Resource<IdResult>>()
    val resendResponse = MutableLiveData<Resource<IdResult>>()

    fun confirm(code: String) {
        viewModelScope.launch {
            confirmResponse.postValue(Resource.loading(null))
            try {
                val idResult = appAuthRepository.confirmCode(code)
                when {
                    idResult.success() -> confirmResponse.postValue(Resource.success(idResult))
                    idResult.failure() -> confirmResponse.postValue(Resource.error(idResult.exception, idResult))
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
                val idResult = appAuthRepository.login(phone)
                when {
                    idResult.success() -> resendResponse.postValue(Resource.success(idResult))
                    idResult.failure() -> resendResponse.postValue(Resource.error(idResult.exception, idResult))
                }
            } catch (t: Throwable) {
                resendResponse.postValue(Resource.error(t, null))
            }
        }
    }

}