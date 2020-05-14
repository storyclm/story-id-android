package ru.breffi.storyid.profile.mapper

import ru.breffi.storyid.profile.FileHelper
import ru.breffi.storyid.profile.db.dto.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyid.profile.model.internal.Metadata

internal class ProfileDbMapper(private val fileHelper: FileHelper, private val metadata: Metadata) {

    fun getProfileModel(
        dbModel: ProfileDbModel?,
        demographicsModel: DemographicsModel,
        itnModel: ItnModel,
        snilsModel: SnilsModel,
        passportModel: PassportModel
    ): ProfileModel {
        return dbModel?.let {
            ProfileModel(
                userId = dbModel.userId,
                phone = dbModel.phone,
                phoneVerified = dbModel.phoneVerified,
                username = dbModel.username,
                email = dbModel.email,
                emailVerified = dbModel.emailVerified,
                demographics = demographicsModel,
                itn = itnModel,
                snils = snilsModel,
                passport = passportModel
            )
        } ?: ProfileModel(
            userId = metadata.userId,
            demographics = demographicsModel,
            itn = itnModel,
            snils = snilsModel,
            passport = passportModel
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

    fun getPassportModel(dbModel: PassportDbModel?, passportPage1Model: PassportPageModel, passportPage2Model: PassportPageModel): PassportModel {
        return dbModel?.let {
            PassportModel(
                sn = dbModel.sn,
                code = dbModel.code,
                issuedBy = dbModel.issuedBy,
                issuedAt = dbModel.issuedAt,
                verified = dbModel.verified,
                pages = listOf(passportPage1Model, passportPage2Model)
            )
        } ?: PassportModel(pages = listOf(passportPage1Model, passportPage2Model))
    }

    fun getPassportPageModel(dbModel: PassportPageDbModel?, page: Int): PassportPageModel {
        return dbModel?.let {
            PassportPageModel(
                page = dbModel.page,
                file = dbModel.fileName?.let { fileHelper.getFile(it) }
            )
        } ?: PassportPageModel(
            page = page
        )
    }
}