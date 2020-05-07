package ru.breffi.storyidsample.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.auth.common.model.AuthError
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyid.auth.common.model.AuthSuccess
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject

class AuthViewModel @Inject
constructor(
    private val appAuthRepository: AppAuthRepository
) : ViewModel() {

    var response = MutableLiveData<Resource<AuthState>>()

    fun register(phone: String) {
        viewModelScope.launch {
            try {
                val authState = appAuthRepository.login(phone)
                when (authState) {
                    AuthSuccess -> response.postValue(Resource.success(authState))
                    is AuthError -> response.postValue(Resource.error(authState.error, authState))
                }
            } catch (t: Throwable) {
                response.postValue(Resource.error(t, null))
            }
        }
    }
}

