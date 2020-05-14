package ru.breffi.storyid.profile.model

data class CreateBankAccountModel(

    val name: String,

    val description: String? = null,

    val settlementAccount: String,

    val bank: String,

    val bic: String,

    val correspondentAccount: String
)