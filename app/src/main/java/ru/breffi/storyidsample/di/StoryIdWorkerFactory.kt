package ru.breffi.storyidsample.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class StoryIdWorkerFactory @Inject constructor(
    private val workers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry: Map.Entry<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>? =
            workers.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }

        val factoryProvider = foundEntry?.value
            ?: throw IllegalArgumentException("unknown worker class name: $workerClassName")

        return factoryProvider.get().create(appContext, workerParameters)
    }
}