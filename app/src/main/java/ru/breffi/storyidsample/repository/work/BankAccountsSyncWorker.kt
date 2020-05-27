package ru.breffi.storyidsample.repository.work

import android.content.Context
import androidx.work.*
import ru.breffi.storyid.auth.flow.passwordless.PasswordlessAuthHandler
import ru.breffi.storyidsample.di.ChildWorkerFactory
import ru.breffi.storyidsample.repository.BankAccountRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BankAccountsSyncWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val bankAccountRepository: BankAccountRepository,
    private val authHandler: PasswordlessAuthHandler
) : CoroutineWorker(context, workerParams) {

    companion object {

        private const val TAG = "bank_accounts_sync_worker"
        const val MAX_ATTEMPT_COUNT = 1

        fun start(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val requestBuilder = OneTimeWorkRequestBuilder<BankAccountsSyncWorker>()
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

        if (authHandler.isAuthenticated()) {
            bankAccountRepository.syncBankAccounts()
        }

        return Result.success()
    }

    class Factory @Inject constructor(
        private val bankAccountRepository: BankAccountRepository,
        private val authHandler: PasswordlessAuthHandler
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return BankAccountsSyncWorker(
                appContext,
                params,
                bankAccountRepository,
                authHandler
            )
        }
    }
}