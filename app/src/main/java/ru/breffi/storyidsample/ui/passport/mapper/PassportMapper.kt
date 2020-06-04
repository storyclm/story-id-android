package ru.breffi.storyidsample.ui.passport.mapper

import ru.breffi.storyid.profile.model.PassportPageModel
import ru.breffi.storyidsample.ui.passport.model.PassportPageUiModel
import java.io.File

class PassportMapper(private val initialPages: List<PassportPageModel>) {

    private var pageMap = mutableMapOf<Int, PassportPageUiModel>()

    fun mapPages(): List<PassportPageUiModel> {
        val initialPageMap = initialPages.associateBy { it.page }
        val pages = IntRange(1, 9).map { index ->
            initialPageMap[index]?.let { PassportPageUiModel(index, it.file) } ?: PassportPageUiModel(index)
        }
        pages.forEach { pageMap[it.page] = it }
        return pages
    }

    fun mapWithPageAdded(file: File): List<PassportPageUiModel> {
        val page = getPageFromFileName(file.name)
        pageMap[page] = pageMap.getValue(page).copy(imageFile = file)
        return pageMap.values.toList()
    }

    fun mapWithPageDeleted(filename: String): List<PassportPageUiModel> {
        val index = pageMap.entries.first { it.value.imageFile?.name == filename }.key
        pageMap[index] = pageMap.getValue(index).copy(imageFile = null)
        return pageMap.values.toList()
    }

    fun getUpdatedPages(): List<PassportPageModel> {
        return pageMap.values
            .map {
                PassportPageModel(it.imageFile, it.page)
            }
            .toList()
    }

    private fun getPageFromFileName(name: String): Int {
        return name.substringBefore(".").toInt()
    }
}