package ru.breffi.storyidsample.ui.bank_accounts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import ru.breffi.storyidsample.repository.BankAccountRepository
import javax.inject.Inject

class BankAccountsViewModel @Inject
constructor(
    private val bankAccountRepository: BankAccountRepository
) : ViewModel() {

    val start = MutableLiveData<Long>()
    val bankAccounts = start.switchMap {
        bankAccountRepository.getBankAccounts()
    }

    fun startLoading() {
        start.postValue(0)
    }
}