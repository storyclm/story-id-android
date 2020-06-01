package ru.breffi.storyidsample.ui.itn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import ru.breffi.storyidsample.ui.common.LiveDataWrapper
import java.io.File
import javax.inject.Inject


class ItnViewModel @Inject
constructor(
    private val context: Context,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val imageItn = MutableLiveData<File?>()


    val profile = LiveDataWrapper(profileRepository.getProfile())

    fun saveProfile(profile: ProfileModel?) {
        viewModelScope.launch {
            if (profile != null) {
                profileRepository.saveProfile(profile)
            }
            ProfileSyncWorker.start(context)
        }
    }

    fun setItnImage(file: File) {
        imageItn.postValue(file)
    }

    fun deleteImage() {
        imageItn.postValue(null)
    }
}