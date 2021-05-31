package ru.breffi.storyidsample.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.auth.common.model.IdResult
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject

class AuthViewModel @Inject
constructor(
    private val appAuthRepository: AppAuthRepository
) : ViewModel() {

    var response = MutableLiveData<Resource<IdResult>>()

    fun register(phone: String) {
        viewModelScope.launch {
            try {
                val idResult = appAuthRepository.login(phone)
                when {
                    idResult.success() -> response.postValue(Resource.success(idResult))
                    idResult.failure() -> response.postValue(Resource.error(idResult.exception, idResult))
                }
            } catch (t: Throwable) {
                response.postValue(Resource.error(t, null))
            }
        }
    }
}

