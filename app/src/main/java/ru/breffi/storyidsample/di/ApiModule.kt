package ru.breffi.storyidsample.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ru.breffi.storyid.auth.common.model.AuthConfig
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthHandler
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthProvider
import ru.breffi.storyid.profile.ProfileInteractor
import ru.breffi.storyid.profile.ProfileInteractorProvider
import ru.breffi.storyid.profile.model.ProfileConfig
import ru.breffi.storyidsample.BuildConfig
import ru.breffi.storyidsample.api.base.ExcludeDeserializationStrategy
import ru.breffi.storyidsample.api.base.ExludeSerializationStrategy
import ru.breffi.storyidsample.api.typeadapter.GmtDateTypeAdapter
import java.util.*
import javax.inject.Singleton


@Module
class ApiModule {

    @Provides
    @Singleton
    internal fun provideAuthProvider(context: Context): PasswordlessAuthProvider {
        val authConfig = AuthConfig(
            BuildConfig.API_ID_CONFIG_URL,
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET,
            "nopass_auth_data_storage"
        )
        return PasswordlessAuthProvider(context, authConfig)
    }

    @Provides
    @Singleton
    internal fun provideAuthFlows(authProvider: PasswordlessAuthProvider): PasswordlessAuthHandler {
        return authProvider.getFlowHandler()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(authProvider: PasswordlessAuthProvider): OkHttpClient {
        return authProvider.getFlowClient()
    }

    @Provides
    @Singleton
    internal fun provideProfileInteractor(context: Context, okHttpClient: OkHttpClient, authHandler: PasswordlessAuthHandler): ProfileInteractor {
        return ProfileInteractorProvider(context, okHttpClient,  ProfileConfig(BuildConfig.API_ID_ENDPOINT), authHandler).get()
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder()
            .addSerializationExclusionStrategy(ExludeSerializationStrategy())
            .addDeserializationExclusionStrategy(ExcludeDeserializationStrategy())
            .addSerializationExclusionStrategy(ExludeSerializationStrategy())
            .registerTypeAdapter(Date::class.java, GmtDateTypeAdapter())
            .serializeNulls()
            .create()
    }
}
