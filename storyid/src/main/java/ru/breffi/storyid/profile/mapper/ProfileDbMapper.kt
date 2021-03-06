package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.db.dto.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyid.profile.model.internal.Metadata

internal class ProfileDbMapper(private val fileHelper: FileHelper, private val metadata: Metadata) {

    fun getProfileModel(
        profileIdModel: ProfileIdModel,
        demographicsModel: DemographicsModel,
        itnModel: ItnModel,
        snilsModel: SnilsModel,
        passportModel: PassportModel
    ): ProfileModel {
        return ProfileModel(
            profileId = profileIdModel,
            demographics = demographicsModel,
            itn = itnModel,
            snils = snilsModel,
            passport = passportModel
        )
    }

    fun getProfileIdModel(dbModel: ProfileDbModel?): ProfileIdModel {
        return dbModel?.let {
            ProfileIdModel(
                userId = dbModel.userId,
                phone = dbModel.phone,
                phoneVerified = dbModel.phoneVerified,
                username = dbModel.username,
                email = dbModel.email,
                emailVerified = dbModel.emailVerified
            )
        } ?: ProfileIdModel(
            userId = metadata.userId
        )
    }

    fun getDemographicsModel(dbModel: DemographicsDbModel?): DemographicsModel {
        return dbModel?.let {
            DemographicsModel(
                name = dbModel.name,
                surname = dbModel.surname,
                patronymic = dbModel.patronymic,
                birthday = dbModel.birthday,
                gender = dbModel.gender,
                verified = dbModel.verified
            )
        } ?: DemographicsModel()
    }

    fun getItnModel(dbModel: ItnDbModel?): ItnModel {
        return dbModel?.let {
            ItnModel(
                itn = dbModel.itn,
                file = dbModel.fileName?.let { fileHelper.getFile(it) }
            )
        } ?:ItnModel()
    }

    fun getSnilsModel(dbModel: SnilsDbModel?): SnilsModel {
        return dbModel?.let {
            SnilsModel(
                snils = dbModel.snils,
                file = dbModel.fileName?.let { fileHelper.getFile(it) }
            )
        } ?: SnilsModel()
    }

    fun getPassportModel(dbModel: PassportDbModel?, passportPageModels: List<PassportPageModel>): PassportModel {
        return dbModel?.let {
            PassportModel(
                passportData = PassportDataModel(
                    sn = dbModel.sn,
                    code = dbModel.code,
                    issuedBy = dbModel.issuedBy,
                    issuedAt = dbModel.issuedAt,
                    verified = dbModel.verified
                ),
                pages = passportPageModels
            )
        } ?: PassportModel(passportData = PassportDataModel(), pages = passportPageModels)
    }

    fun getPassportPageModel(dbModel: PassportPageDbModel): PassportPageModel {
        return PassportPageModel(page = dbModel.page, file = dbModel.fileName?.let { fileHelper.getFile(it) })
    }
}