package ru.breffi.storyidsample.ui.bank_account

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import ru.breffi.storyidsample.repository.BankAccountRepository
import ru.breffi.storyidsample.valueobject.Resource
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import javax.inject.Inject

class BankAccountViewModel @Inject
constructor(
    private val bankAccountRepository: BankAccountRepository
) : ViewModel() {

    val bankAccountId = MutableLiveData<String>()
    val bankAccount = bankAccountId.switchMap {
        bankAccountRepository.getBankAccountLiveData(it)
    }

    val bankAccountCreateRequest = MutableLiveData<CreateBankAccountModel>()
    val bankAccountUpdateRequest = MutableLiveData<BankAccountModel>()

    val bankAccountResponse = MediatorLiveData<Resource<BankAccountModel>>()

    init {
        bankAccountResponse.addSource(
            bankAccountCreateRequest.switchMap {
                bankAccountRepository.createBankAccount(it)
            }
        ) { value -> bankAccountResponse.setValue(value) }
        bankAccountResponse.addSource(
            bankAccountUpdateRequest.switchMap {
                bankAccountRepository.changeBankAccount(it)
            }
        ) { value -> bankAccountResponse.setValue(value) }
    }

}