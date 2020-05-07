package ru.breffi.storyidsample.ui.splash

import androidx.lifecycle.ViewModel
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthHandler
import ru.breffi.storyidsample.repository.EncryptSharedPreferences
import javax.inject.Inject

class SplashViewModel @Inject
constructor(
    private val authHandler: PasswordlessAuthHandler,
    private val preferencesRepository: EncryptSharedPreferences
) : ViewModel() {

    fun isAuth(): Boolean {
        return authHandler.isAuthenticated()
    }

    fun pinExist(): Boolean {
        return preferencesRepository.pinIsExist()
    }
}