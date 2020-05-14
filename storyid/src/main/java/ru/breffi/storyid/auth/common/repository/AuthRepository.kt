package ru.breffi.storyid.auth.common.repository

import ru.breffi.storyid.auth.common.model.AuthData
import ru.breffi.storyid.auth.common.storage.AuthEncryptedStorage

internal class AuthRepository(private val authStorage: AuthEncryptedStorage) {

    fun getAuthData(): AuthData? {
        return authStorage.getAuthData()
    }

    fun saveAuthData(authData: AuthData) {
        authStorage.saveAuthData(authData)
    }

    fun clearAuthData() {
        authStorage.resetAuthorization()
    }
}