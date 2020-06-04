package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.PassportPageDbModel

internal data class PassportPageUpdateModel(val page: Int, val dbModel: PassportPageDbModel?, val fileAction: FileAction)