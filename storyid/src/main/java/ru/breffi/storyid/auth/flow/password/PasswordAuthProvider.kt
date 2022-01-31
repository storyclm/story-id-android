package ru.breffi.storyid.auth.flow.password

import android.content.Context
import okhttp3.OkHttpClient
import ru.breffi.storyid.auth.common.ClientFactory
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.common.repository.AuthRepository
import ru.breffi.storyid.auth.common.storage.AuthEncryptedStorage

class PasswordAuthProvider(context: Context, authConfig: AuthConfig) {

    private val authentication = PasswordAuthentication(authConfig, AuthRepository(AuthEncryptedStorage(context, authConfig.authStorageName)))
    private val client = ClientFactory.createDefaultClient(authentication, authConfig.trustAll)

    fun getFlowHandler(): PasswordAuthHandler {
        return authentication
    }

    fun getFlowClient(): OkHttpClient {
        return client
    }
}