package ru.breffi.storyid.profile.model

data class BankAccountModel(

    val internalId: String,

    val profileId: String? = null,

    val verified: Boolean? = null,

    val name: String,

    val description: String? = null,

    val settlementAccount: String,

    val bank: String,

    val bic: String,

    val correspondentAccount: String
)