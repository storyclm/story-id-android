package ru.breffi.storyidsample.ui.profile

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.breffi.storyid.profile.model.FileModel
import ru.breffi.storyidsample.repository.AppAuthRepository
import ru.breffi.storyidsample.repository.FilesRepository
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.ui.profile.ProfileFragment.Companion.AVATAR_IMAGE_FILE
import java.io.File
import javax.inject.Inject

class ProfileViewModel @Inject
constructor(
    private val profileRepository: ProfileRepository,
    private val appAuthRepository: AppAuthRepository
) : ViewModel(), Observer<FileModel?> {

    val imageAvatar = MutableLiveData<File>()

    val profile = profileRepository.getProfile()

    fun startLoading() {
        profileRepository.getAvatar().observeForever(this)
    }

    fun setAvatarImage(file: File) {
        viewModelScope.launch {
            profileRepository.setAvatar(file)
        }
        imageAvatar.postValue(file)
    }

    fun deleteAvatarImage() {
        viewModelScope.launch {
            profileRepository.deleteAvatar()
        }
        imageAvatar.postValue(null)
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            appAuthRepository.clearUserData()
        }
    }

    override fun onCleared() {
        profileRepository.getAvatar().removeObserver(this)
        super.onCleared()
    }

    override fun onChanged(fileModel: FileModel?) {
        fileModel?.file?.let {
            imageAvatar.postValue(it)
        }
    }
}