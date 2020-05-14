package ru.breffi.storyid.profile

import okhttp3.OkHttpClient
import ru.breffi.storyid.generated_api.ApiClient
import ru.breffi.storyid.generated_api.api.*

internal class ApiServiceProvider(client: OkHttpClient, baseApiUrl: String) {

    private val apiClient = ApiClient()

    private lateinit var profileApi: ProfileApi
    private lateinit var profileDemographicsApi: ProfileDemographicsApi
    private lateinit var profileItnApi: ProfileItnApi
    private lateinit var profileSnilsApi: ProfileSnilsApi
    private lateinit var profilePassportApi: ProfilePasportApi
    private lateinit var profileBankAccountsApi: ProfileBankAccountsApi

    private lateinit var auxApi: AuxApi

    init {
        apiClient.adapterBuilder = apiClient.adapterBuilder
            .baseUrl(baseApiUrl)
            .client(client)
        apiClient.configureFromOkclient(client)
    }

    fun getProfileApi(): ProfileApi {
        if (!::profileApi.isInitialized) {
            profileApi = apiClient.createService(ProfileApi::class.java)
        }
        return profileApi
    }

    fun getProfileDemographicsApi(): ProfileDemographicsApi {
        if (!::profileDemographicsApi.isInitialized) {
            profileDemographicsApi = apiClient.createService(ProfileDemographicsApi::class.java)
        }
        return profileDemographicsApi
    }

    fun getProfileItnApi(): ProfileItnApi {
        if (!::profileItnApi.isInitialized) {
            profileItnApi = apiClient.createService(ProfileItnApi::class.java)
        }
        return profileItnApi
    }

    fun getProfileSnilsApi(): ProfileSnilsApi {
        if (!::profileSnilsApi.isInitialized) {
            profileSnilsApi = apiClient.createService(ProfileSnilsApi::class.java)
        }
        return profileSnilsApi
    }

    fun getProfilePassportApi(): ProfilePasportApi {
        if (!::profilePassportApi.isInitialized) {
            profilePassportApi = apiClient.createService(ProfilePasportApi::class.java)
        }
        return profilePassportApi
    }

    fun getProfileBankAccountsApi(): ProfileBankAccountsApi {
        if (!::profileBankAccountsApi.isInitialized) {
            profileBankAccountsApi = apiClient.createService(ProfileBankAccountsApi::class.java)
        }
        return profileBankAccountsApi
    }

    fun getAuxApi(): AuxApi {
        if (!::auxApi.isInitialized) {
            auxApi = apiClient.createService(AuxApi::class.java)
        }
        return auxApi
    }
}