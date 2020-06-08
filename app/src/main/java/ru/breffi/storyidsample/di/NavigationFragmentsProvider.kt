package ru.breffi.storyidsample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.breffi.storyidsample.ui.bank_account.BankAccountFragment
import ru.breffi.storyidsample.ui.bank_accounts.BankAccountsFragment
import ru.breffi.storyidsample.ui.itn.ItnFragment
import ru.breffi.storyidsample.ui.passport.PassportFragment
import ru.breffi.storyidsample.ui.personal_data.PersonalDataFragment
import ru.breffi.storyidsample.ui.profile.ProfileFragment
import ru.breffi.storyidsample.ui.snils.SnilsFragment

@Module
abstract class NavigationFragmentsProvider {

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributePersonalDataFragment(): PersonalDataFragment

    @ContributesAndroidInjector
    abstract fun contributeItnFragment(): ItnFragment

    @ContributesAndroidInjector
    abstract fun contributeSnilsFragment(): SnilsFragment

    @ContributesAndroidInjector
    abstract fun contributePassportFragment(): PassportFragment

    @ContributesAndroidInjector
    abstract fun contributeBankAccountsFragment(): BankAccountsFragment

    @ContributesAndroidInjector
    abstract fun contributeBankAccountFragment(): BankAccountFragment
}