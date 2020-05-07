package ru.breffi.storyidsample.repository.work

import android.content.Context
import android.content.Intent
import androidx.work.*
import ru.breffi.storyidsample.di.ChildWorkerFactory
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthHandler
import ru.breffi.storyidsample.repository.ProfileRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileSyncWorker @Inject
constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val profileRepository: ProfileRepository,
    private val authHandler: PasswordlessAuthHandler
) : CoroutineWorker(context, workerParams) {

    companion object {

        private const val TAG = "profile_sync_worker"
        const val MAX_ATTEMPT_COUNT = 1

        const val BROADCAST_ACTION = "ACTION.syncProfileComplete"

        fun start(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val requestBuilder = OneTimeWorkRequestBuilder<ProfileSyncWorker>()
                .addTag(TAG)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.SECONDS)
                .setConstraints(constraints)

            val request: OneTimeWorkRequest = requestBuilder.build()

            with(WorkManager.getInstance(context)) {
                cancelAllWorkByTag(TAG)
                enqueue(request)
            }
        }

        fun cancel(context: Context) {
            with(WorkManager.getInstance(context)) {
                cancelAllWorkByTag(TAG)
            }
        }

    }

    override suspend fun doWork(): Result {

        if (runAttemptCount > MAX_ATTEMPT_COUNT) {
            return Result.failure()
        }

        var errors = 0

        if (authHandler.isAuthenticated()) {
            profileRepository.syncProfile()
            errors += profileRepository.syncAvatarImage()
        }

        applicationContext.sendBroadcast(Intent(BROADCAST_ACTION))

        return if (errors == 0) {
            Result.success()
        } else {
            Result.retry()
        }
    }


    class Factory @Inject constructor(
        private val profileRepository: ProfileRepository,
        private val authHandler: PasswordlessAuthHandler
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return ProfileSyncWorker(
                appContext,
                params,
                profileRepository,
                authHandler
            )
        }
    }
}