package ru.breffi.storyid.profile.model

data class PassportModel(

    val passportData: PassportDataModel,

    val pages: List<PassportPageModel>
) {

    fun page(index: Int): PassportPageModel? {
        return pages.find { it.page == index }
    }
}