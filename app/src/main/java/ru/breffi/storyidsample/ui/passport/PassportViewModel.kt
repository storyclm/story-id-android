package ru.breffi.storyidsample.ui.passport

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import ru.breffi.storyidsample.ui.common.LiveDataWrapper
import javax.inject.Inject


class PassportViewModel @Inject
constructor(
    private val context: Context,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val profile = LiveDataWrapper(profileRepository.getProfile())

    fun saveProfile(profile: ProfileModel?) {
        viewModelScope.launch {
            if (profile != null) {
                profileRepository.saveProfile(profile)
            }
            ProfileSyncWorker.start(context)
        }
    }
}