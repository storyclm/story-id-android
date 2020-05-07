package ru.breffi.storyidsample.ui.passport.model

import java.io.File

class PassportPage(

    val page: Int,

    var imagePath: String,

    var imageFile: File? = null,

    var action: Int,

    var deleted: Int = 0

) {

    companion object {
        const val ADD = 1
        const val CHANGE = 2
        const val REMOVE = 3
    }

}