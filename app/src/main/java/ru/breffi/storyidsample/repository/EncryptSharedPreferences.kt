package ru.breffi.storyidsample.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EncryptSharedPreferences @Inject
constructor(context: Context) {

    companion object {
        const val PREFS_PIN_CODE = "key_pin_code"
    }

    private val prefs: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        prefs = EncryptedSharedPreferences.create(
            "token_prefs.xml",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun <T> save(key: String, value: T) {
        prefs.edit().putString(key, value.toString()).apply()
    }

    fun clear() {
        prefs.edit().remove(PREFS_PIN_CODE).apply()
    }

    fun setPinCode(pin: String) {
        save(PREFS_PIN_CODE, pin)
    }

    fun checkPin(pin: String): Boolean {
        return pin == prefs.getString(PREFS_PIN_CODE, null)
    }

    fun pinIsExist(): Boolean {
        return prefs.getString(PREFS_PIN_CODE, null) != null
    }

    fun getPinCode(): String? {
        return prefs.getString(PREFS_PIN_CODE, null)
    }

    fun resetPin() {
        prefs.edit().remove(PREFS_PIN_CODE).apply()
    }

    fun saveValueByKey(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getStringValueByKey(key: String): String? {
        return prefs.getString(key, null)
    }
}