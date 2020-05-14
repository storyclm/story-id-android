package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.ItnDbModel

internal data class ItnUpdateModel(val dbModel: ItnDbModel, val fileAction: FileAction)