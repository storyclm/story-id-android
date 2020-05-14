package ru.breffi.storyidsample.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(ProfileSyncWorker::class)
    internal abstract fun bindProfileSyncWorker(factory: ProfileSyncWorker.Factory): ChildWorkerFactory
}