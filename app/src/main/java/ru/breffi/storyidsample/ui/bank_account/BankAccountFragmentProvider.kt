package ru.breffi.storyidsample.ui.bank_account

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BankAccountFragmentProvider {

    @ContributesAndroidInjector
    abstract fun contributeBankAccountFragment(): BankAccountFragment

}