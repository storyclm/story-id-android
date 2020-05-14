package ru.breffi.storyidsample.di


import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.breffi.storyidsample.ui.auth.AuthActivity
import ru.breffi.storyidsample.ui.confirm_code.ConfirmCodeActivity
import ru.breffi.storyidsample.ui.common.navigation.NavigationActivity
import ru.breffi.storyidsample.ui.pin_code.PinCodeActivity
import ru.breffi.storyidsample.ui.splash.SplashActivity
import ru.breffi.storyidsample.ui.image_preview.ImagePreviewActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeConfirmCodeActivity(): ConfirmCodeActivity

    @ContributesAndroidInjector
    abstract fun contributePinCodeActivity(): PinCodeActivity

    @ContributesAndroidInjector
    abstract fun contributeImagePreviewActivity(): ImagePreviewActivity

    @ContributesAndroidInjector(modules = [NavigationFragmentsProvider::class])
    abstract fun contributeNavigationActivity(): NavigationActivity
}