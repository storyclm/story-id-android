package ru.breffi.storyidsample.repository.permissons

import androidx.lifecycle.ViewModel
import ru.breffi.storyidsample.repository.permissons.PermissionResult
import kotlinx.coroutines.channels.Channel

internal class SuspendPermissionViewModel : ViewModel() {
    val channel = Channel<PermissionResult>(Channel.UNLIMITED)

    override fun onCleared() {
        super.onCleared()
        channel.close()
    }
}