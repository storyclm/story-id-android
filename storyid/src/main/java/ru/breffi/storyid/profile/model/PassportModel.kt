package ru.breffi.storyid.profile.model

data class PassportModel(

    val verified: Boolean? = null,

    var sn: String? = null,

    val issuedBy: String? = null,

    val issuedAt: Long? = null,

    var code: String? = null,

    val pages: List<PassportPageModel>
) {

    fun page(index: Int): PassportPageModel? {
        return pages.find { it.page == index }
    }
}