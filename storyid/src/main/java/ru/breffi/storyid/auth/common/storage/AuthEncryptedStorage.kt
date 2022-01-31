package ru.breffi.storyid.auth.common.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.breffi.storyid.auth.common.model.AuthData


internal class AuthEncryptedStorage(context: Context, authStorageName: String) {

    companion object {

        const val KEY_AUTH_DATA = "key_auth_data"
    }

    private val gson = Gson()
    private val prefs: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        prefs = EncryptedSharedPreferences.create(
            authStorageName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAuthData(authData: AuthData) {
        val dataString = gson.toJson(authData)
        prefs.edit().putString(KEY_AUTH_DATA, dataString).apply()
    }

    fun getAuthData(): AuthData? {
        return prefs.getString(KEY_AUTH_DATA, null)?.let { dataString ->
            try {
                gson.fromJson(dataString, AuthData::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }
    }

    fun resetAuthorization() {
        prefs.edit().remove(KEY_AUTH_DATA).apply()
    }
}