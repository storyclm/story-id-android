package ru.breffi.storyid.profile.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts_data")
data class BankAccountDbModel(

    @PrimaryKey
    val internalId: String,

    val id: String? = null,

    val profileId: String? = null,

    val modifiedAt: Long,

    val modifiedBy: String,

    val verified: Boolean? = null,

    val verifiedAt: Long? = null,

    val verifiedBy: String? = null,

    val name: String,

    val description: String? = null,

    val settlementAccount: String,

    val bank: String,

    val bic: String,

    val correspondentAccount: String,

    val userId: String,

    val deleted: Boolean = false
)