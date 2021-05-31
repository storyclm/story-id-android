package ru.breffi.storyidsample.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.breffi.storyid.auth.common.model.IdResult
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthHandler
import ru.breffi.storyid.profile.ProfileInteractor
import ru.breffi.storyidsample.utils.getDigits
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuthRepository @Inject
internal constructor(
    private val filesRepository: FilesRepository,
    private val authHandler: PasswordlessAuthHandler,
    private val encryptSharedPreferences: EncryptSharedPreferences,
    private val profileInteractor: ProfileInteractor
) {

    suspend fun login(phone: String): IdResult {
        return withContext(Dispatchers.IO) {
            authHandler.passwordlessAuth(phone.getDigits())
        }
    }

    suspend fun confirmCode(code: String): IdResult {
        return withContext(Dispatchers.IO) {
            authHandler.passwordlessProceedWithCode(code)
        }
    }

    fun getUserId(): String? {
        return authHandler.getAuthData()?.userId
    }

    suspend fun clearUserData() {
        withContext(Dispatchers.IO) {
            profileInteractor.removeUserData()
            authHandler.logout()
            filesRepository.clearFiles()
            encryptSharedPreferences.clear()
        }
    }
}