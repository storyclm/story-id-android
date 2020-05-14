package ru.breffi.storyid.profile.db

import androidx.room.*
import ru.breffi.storyid.profile.db.dto.BankAccountDbModel

@Dao
abstract class BankAccountDataDao {

    @Query("DELETE FROM bank_accounts_data")
    abstract fun deleteAll()

    @Query("DELETE FROM bank_accounts_data WHERE id = :id")
    abstract fun deleteById(id: String)

    @Query("SELECT * FROM bank_accounts_data WHERE userId = :userId ORDER BY id ASC")
    abstract fun getUserBankAccounts(userId: String): List<BankAccountDbModel>

    @Query("SELECT * FROM bank_accounts_data ORDER BY id ASC")
    abstract fun getAll(): List<BankAccountDbModel>

    @Query("SELECT * FROM bank_accounts_data WHERE internalId = :internalId")
    abstract fun getByInternalId(internalId: String): BankAccountDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(bankAccounts: List<BankAccountDbModel>)

    @Update
    abstract fun updateAll(bankAccounts: List<BankAccountDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(bankAccount: BankAccountDbModel)

}