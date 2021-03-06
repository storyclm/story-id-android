package ru.breffi.storyidsample.ui.snils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import ru.breffi.storyidsample.ui.common.LiveDataWrapper
import java.io.File
import javax.inject.Inject


class SnilsViewModel @Inject
constructor(
    private val context: Context,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val imageSnils = MutableLiveData<File?>()

    val profile = LiveDataWrapper(profileRepository.getProfile())

    fun saveProfile(profile: ProfileModel?) {
        viewModelScope.launch {
            if (profile != null) {
                profileRepository.saveProfile(profile)
            }
            ProfileSyncWorker.start(context)
        }
    }

    fun setSnilsImage(file: File) {
        imageSnils.postValue(file)
    }

    fun deleteImage() {
        imageSnils.postValue(null)
    }
}