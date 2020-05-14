package ru.breffi.storyid.profile.model

data class PassportModel(

    val verified: Boolean? = null,

    var sn: String? = null,

    val issuedBy: String? = null,

    val issuedAt: Long? = null,

    var code: String? = null,

    val pages: List<PassportPageModel>
) {

    fun page1(): PassportPageModel? {
        return pages.find { it.page == 1 }
    }

    fun page2(): PassportPageModel? {
        return pages.find { it.page == 2 }
    }
}