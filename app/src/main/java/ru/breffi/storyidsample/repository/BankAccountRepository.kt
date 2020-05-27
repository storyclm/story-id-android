package ru.breffi.storyidsample.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.breffi.storyid.profile.ProfileInteractor
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankAccountRepository @Inject
internal constructor(private val profileInteractor: ProfileInteractor) {

    fun getBankAccounts(): LiveData<Resource<List<BankAccountModel>>> {
        return liveData {
            emit(Resource.loading<List<BankAccountModel>>(null))
            val bankAccounts = withContext(Dispatchers.IO) {
                profileInteractor.getBankAccounts()
            }
            emit(Resource.success(bankAccounts))
        }
    }

    fun getBankAccountLiveData(id: String): LiveData<BankAccountModel?> {
        return liveData {
            val bankAccount = withContext(Dispatchers.IO) {
                profileInteractor.getBankAccount(id)
            }
            emit(bankAccount)
        }
    }

    suspend fun getBankAccount(id: String): BankAccountModel? {
        return withContext(Dispatchers.IO) {
            profileInteractor.getBankAccount(id)
        }
    }

    fun createBankAccount(createBankAccount: CreateBankAccountModel): LiveData<Resource<BankAccountModel>> {
        return liveData {
            val bankAccount = withContext(Dispatchers.IO) {
                profileInteractor.createBankAccount(createBankAccount)
            }
            if (bankAccount != null) {
                emit(Resource.success(bankAccount))
            } else {
                emit(Resource.error<BankAccountModel>(Exception(), bankAccount))
            }
        }
    }

    fun changeBankAccount(bankAccount: BankAccountModel): LiveData<Resource<BankAccountModel>> {
        return liveData {
            withContext(Dispatchers.IO) {
                profileInteractor.updateBankAccount(bankAccount)
            }
            emit(Resource.success(bankAccount))
        }
    }

    fun syncBankAccounts() {
        profileInteractor.syncBankAccounts()
    }
}