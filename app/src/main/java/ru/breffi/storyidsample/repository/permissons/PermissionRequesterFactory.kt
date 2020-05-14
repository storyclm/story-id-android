package ru.breffi.storyidsample.repository.permissons

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

internal interface PermissionRequesterFactory {

    fun getRequesterAsync(context: Context): Deferred<PermissionRequester>

    companion object {
        val defaultFactory: PermissionRequesterFactory = PermissionRequesterFactoryImpl()
    }
}

private class PermissionRequesterFactoryImpl : PermissionRequesterFactory {
    override fun getRequesterAsync(context: Context): Deferred<PermissionRequester> {
        val completableDeferred = CompletableDeferred<PermissionRequester>()
        SuspendPermissionActivity.requesterDeferred = completableDeferred
        val intent = Intent(context, SuspendPermissionActivity::class.java)
        context.startActivity(intent)
        return completableDeferred
    }
}