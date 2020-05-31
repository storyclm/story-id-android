package ru.breffi.storyid.profile

import android.content.Context
import okhttp3.OkHttpClient
import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.profile.api.ApiServiceProvider
import ru.breffi.storyid.profile.db.StoryIdDatabase
import ru.breffi.storyid.profile.model.ProfileConfig
import ru.breffi.storyid.profile.util.FileHelper

class ProfileInteractorProvider(
    private val context: Context,
    okHttpClient: OkHttpClient,
    profileConfig: ProfileConfig,
    private val authDataProvider: AuthDataProvider
) {

    private val apiServiceProvider = ApiServiceProvider(okHttpClient, profileConfig.apiBaseUrl)
    private val storyIdDatabase = StoryIdDatabase.build(context)
    private val profileDataDao = storyIdDatabase.profileDao()
    private val bankAccountsDataDao = storyIdDatabase.bankAccountDao()
    private val filesDataDao = storyIdDatabase.filesDao()

    private lateinit var profileInteractor: ProfileInteractor

    fun get(): ProfileInteractor {
        if (!::profileInteractor.isInitialized) {
            profileInteractor = ProfileInteractor(
                apiServiceProvider,
                profileDataDao,
                bankAccountsDataDao,
                filesDataDao,
                authDataProvider,
                FileHelper(context)
            )
        }
        return profileInteractor
    }
}