package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.FileDbModel

internal data class FileUpdateModel(val dbModel: FileDbModel, val fileAction: FileAction)