package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.SnilsDbModel

internal data class SnilsUpdateModel(val dbModel: SnilsDbModel, val fileAction: FileAction)