package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.PassportPageDbModel

internal data class PassportPageUpdateModel(val dbModel: PassportPageDbModel, val fileAction: FileAction)