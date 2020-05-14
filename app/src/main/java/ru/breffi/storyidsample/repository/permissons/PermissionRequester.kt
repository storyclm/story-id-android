package ru.breffi.storyidsample.repository.permissons

import kotlinx.coroutines.channels.ReceiveChannel

internal interface PermissionRequester {
    fun requestPermissions(permissions: Array<out String>)
    fun finish()
    val resultsChannel: ReceiveChannel<PermissionResult>
}