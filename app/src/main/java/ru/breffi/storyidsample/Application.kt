package ru.breffi.storyidsample

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import androidx.work.Configuration
import androidx.work.WorkManager
import com.github.piasy.biv.BigImageViewer
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import ru.breffi.storyidsample.di.DaggerAppComponent
import ru.breffi.storyidsample.di.StoryIdWorkerFactory
import ru.breffi.storyidsample.repository.work.BankAccountsSyncWorker
import ru.breffi.storyidsample.ui.common.glide.GlideFileLoader
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import javax.inject.Inject


class Application : Application(), HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var dispatchingBroadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var myWorkerFactory: StoryIdWorkerFactory

    override fun onCreate() {
        super.onCreate()

        //Слушает сворачивание/разворачивание приложения
        Foreground[this].addListener(object : Foreground.Listener {
            override fun onBecameForeground() {
                ProfileSyncWorker.start(applicationContext)
                BankAccountsSyncWorker.start(applicationContext)
            }

            override fun onBecameBackground() {}

        })

        BigImageViewer.initialize(GlideFileLoader.with(this))

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        WorkManager.initialize(
            this, Configuration.Builder()
                .setWorkerFactory(myWorkerFactory)
                .build()
        )
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingActivityInjector
    }

    override fun serviceInjector(): DispatchingAndroidInjector<Service>? {
        return dispatchingServiceInjector
    }

    override fun broadcastReceiverInjector(): DispatchingAndroidInjector<BroadcastReceiver>? {
        return dispatchingBroadcastReceiverInjector
    }

    override fun onTerminate() {
        ProfileSyncWorker.cancel(applicationContext)

        super.onTerminate()
    }
}