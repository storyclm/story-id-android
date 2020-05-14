package ru.breffi.storyidsample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.breffi.storyidsample.ui.confirm_code.ConfirmCodeViewModel
import ru.breffi.storyidsample.ui.pin_code.PinCodeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.breffi.storyidsample.ui.auth.AuthViewModel
import ru.breffi.storyidsample.ui.itn.ItnViewModel
import ru.breffi.storyidsample.ui.passport.PassportViewModel
import ru.breffi.storyidsample.ui.personal_data.PersonalDataViewModel
import ru.breffi.storyidsample.ui.profile.ProfileViewModel
import ru.breffi.storyidsample.ui.snils.SnilsViewModel
import ru.breffi.storyidsample.ui.splash.SplashViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmCodeViewModel::class)
    abstract fun bindConfirmCodeViewModel(viewModel: ConfirmCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinCodeViewModel::class)
    abstract fun bindPinCodeViewModel(viewModel: PinCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PersonalDataViewModel::class)
    abstract fun bindPersonalDataViewModel(viewModel: PersonalDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItnViewModel::class)
    abstract fun bindBankItnViewModel(viewModel: ItnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SnilsViewModel::class)
    abstract fun bindSnilsViewModel(viewModel: SnilsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PassportViewModel::class)
    abstract fun bindPassportViewModel(viewModel: PassportViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}