package ru.breffi.storyidsample.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.breffi.storyidsample.ui.auth.AuthActivity
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import ru.breffi.storyidsample.ui.common.navigation.NavigationActivity
import ru.breffi.storyidsample.ui.pin_code.PinCodeActivity
import ru.breffi.storyidsample.R
import javax.inject.Inject

class SplashActivity : BaseInjectableActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (viewModel.isAuth()) {
            if (viewModel.pinExist()) {
                PinCodeActivity.start(this, PinCodeActivity.MODE_CHECK)
                finish()
            } else {
                startHome()
            }
        } else {
            startAuth()
        }
    }

    private fun startHome() {
        NavigationActivity.start(this, intent, true)
        finish()
    }

    private fun startAuth() {
        AuthActivity.start(this)
        finish()
    }
}
