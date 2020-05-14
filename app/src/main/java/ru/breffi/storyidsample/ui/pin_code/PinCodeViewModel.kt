package ru.breffi.storyidsample.ui.pin_code

import androidx.lifecycle.ViewModel
import ru.breffi.storyidsample.repository.EncryptSharedPreferences
import javax.inject.Inject

class PinCodeViewModel @Inject
constructor(
    private val preferencesRepository: EncryptSharedPreferences
) : ViewModel() {

    fun setPin(pin: String) {
        preferencesRepository.setPinCode(pin)
    }

    fun checkPin(pin: String): Boolean {
        return preferencesRepository.checkPin(pin)
    }
}