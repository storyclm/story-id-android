package ru.breffi.storyid.profile.model.internal

import ru.breffi.storyid.profile.db.dto.PassportDbModel

internal data class PassportUpdateModel(val dbModel: PassportDbModel, val pages: List<PassportPageUpdateModel>)