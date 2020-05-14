package ru.breffi.storyidsample.ui.profile

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.repository.FilesRepository
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.ui.profile.ProfileFragment.Companion.AVATAR_IMAGE_FILE
import java.io.File
import javax.inject.Inject

class ProfileViewModel @Inject
constructor(
    private val fileRepository: FilesRepository,
    private val profileRepository: ProfileRepository,
    private val appAuthRepository: AppAuthRepository
) : ViewModel() {

    val imageAvatar = MutableLiveData<File>()

    private val start = MutableLiveData<Long>()

    val profile = profileRepository.getProfile()

    fun startLoading() {
        start.postValue(0L)
        getAvatarImage()
    }

    private fun getAvatarImage() {
        viewModelScope.launch {
            try {
                imageAvatar.postValue(fileRepository.getFileIfExists(AVATAR_IMAGE_FILE))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun setAvatarImage(file: File) {
        imageAvatar.postValue(file)
    }

    fun deleteAvatarImage(fileName: String) {
        fileRepository.delete(fileName)
        imageAvatar.postValue(null)
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            appAuthRepository.clearUserData()
        }
    }
}