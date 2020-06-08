package ru.breffi.storyidsample.ui.passport.model

import com.moqod.android.recycler.diff.DiffEntity
import java.io.File

data class PassportPageUiModel(
    val page: Int,
    val imageFile: File? = null
) : DiffEntity {

    override fun areItemsTheSame(entity: DiffEntity?): Boolean {
        return entity is PassportPageUiModel
    }

    override fun areContentsTheSame(entity: DiffEntity?): Boolean {
        return equals(entity)
    }
}