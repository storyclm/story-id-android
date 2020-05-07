package ru.breffi.storyid.auth.flow.passwordless

import android.content.Context
import okhttp3.OkHttpClient
import ru.breffi.storyid.auth.common.ClientFactory
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.common.repository.AuthRepository
import ru.breffi.storyid.auth.common.storage.AuthEncryptedStorage

class PasswordlessAuthProvider(context: Context, authConfig: AuthConfig) {

    private val authentication = PasswordlessAuthentication(authConfig, AuthRepository(AuthEncryptedStorage(context, authConfig.authStorageName)))
    private val client = ClientFactory.createDefaultClient(authentication)

    fun getFlowHandler(): PasswordlessAuthHandler {
        return authentication
    }

    fun getFlowClient(): OkHttpClient {
        return client
    }
}