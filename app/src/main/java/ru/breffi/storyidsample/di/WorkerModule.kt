package ru.breffi.storyidsample.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.breffi.storyidsample.repository.work.BankAccountsSyncWorker
import ru.breffi.storyidsample.repository.work.FilesSyncWorker
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(ProfileSyncWorker::class)
    internal abstract fun bindProfileSyncWorker(factory: ProfileSyncWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(BankAccountsSyncWorker::class)
    internal abstract fun bindBankAccountsSyncWorker(factory: BankAccountsSyncWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(FilesSyncWorker::class)
    internal abstract fun bindFilesSyncWorker(factory: FilesSyncWorker.Factory): ChildWorkerFactory
}