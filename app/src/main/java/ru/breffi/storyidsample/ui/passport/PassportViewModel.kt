package ru.breffi.storyidsample.ui.passport

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.breffi.storyid.profile.model.PassportPageModel
import ru.breffi.storyid.profile.model.ProfileModel
import ru.breffi.storyidsample.repository.ProfileRepository
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import ru.breffi.storyidsample.ui.passport.model.PassportPage
import java.io.File
import javax.inject.Inject


class PassportViewModel @Inject
constructor(
    private val context: Context,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val start = MutableLiveData<Long>()

    val passportPageImages = MutableLiveData<MutableMap<Int, PassportPage>>(mutableMapOf())

    val profile = start.switchMap { profileRepository.getProfile() }

    fun start() {
        start.postValue(0L)
    }

    fun saveProfile(profile: ProfileModel?) {
        viewModelScope.launch {
            if (profile != null) {
                profileRepository.saveProfile(profile)
            }
            ProfileSyncWorker.start(context)
        }
    }

    fun setImage(file: File) {
        val map = passportPageImages.value ?: mutableMapOf()
        val page = getPageFromFileName(file.name)
        map[page] = PassportPage(
            page = page,
            imagePath = file.path,
            imageFile = file,
            action = PassportPage.ADD
        )
        passportPageImages.postValue(map)
    }

    fun deleteImage(fileName: String) {
        val map = (passportPageImages.value ?: mutableMapOf())
            .map { pageModel ->
                val model = pageModel.value
                if (fileName == model.imageFile?.name) {
                    model.action = PassportPage.REMOVE
                    model.deleted = 1
                    model.imageFile = null
                }
                model
            }
            .associateBy { it.page }
            .toMutableMap()

        passportPageImages.postValue(map)
    }

    fun setPassportImages(pageModels: List<PassportPageModel>) {
        viewModelScope.launch {
            try {
                val pages = pageModels
                    .associateBy({ it.page })
                    { model ->
                        PassportPage(
                            page = model.page,
                            deleted = if (model.file == null) 1 else 0,
                            action = if (model.file == null) PassportPage.REMOVE else PassportPage.ADD,
                            imageFile = model.file,
                            imagePath = model.file?.path ?: ""
                        )
                    }
                    .toMutableMap()

                passportPageImages.postValue(pages)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun getPassportFileName(): String {
        return passportPageImages.value
            ?.values
            ?.find { it.imageFile == null }
            ?.page
            ?.let { page -> "$page.jpg" }
            ?: "1.jpg"
    }

    private fun getPageFromFileName(name: String): Int {
        return name.substringBefore(".").toInt()
    }
}