package ru.breffi.storyid.profile.db

import androidx.room.*
import ru.breffi.storyid.profile.db.dto.*

@Dao
abstract class ProfileDataDao {

    //profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProfile(profile: ProfileDbModel)

    @Query("SELECT * FROM profile_data WHERE internalId = :internalId")
    abstract fun getProfileById(internalId: String): ProfileDbModel?

    @Query("SELECT * FROM profile_data WHERE userId = :userId")
    abstract fun getUserProfile(userId: String): ProfileDbModel?

    @Query("DELETE FROM profile_data")
    abstract fun deleteProfile()

    //demographics
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDemographics(demographics: DemographicsDbModel)

    @Query("SELECT * FROM demographics_data WHERE internalId = :internalId")
    abstract fun getDemographicsById(internalId: String): DemographicsDbModel?

    @Query("SELECT * FROM demographics_data WHERE userId = :userId")
    abstract fun getUserDemographics(userId: String): DemographicsDbModel?

    @Query("DELETE FROM demographics_data")
    abstract fun deleteDemographics()

    //ITN
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItn(itn: ItnDbModel)

    @Query("SELECT * FROM itn_data WHERE internalId = :internalId")
    abstract fun getItnById(internalId: String): ItnDbModel?

    @Query("SELECT * FROM itn_data WHERE userId = :userId")
    abstract fun getUserItn(userId: String): ItnDbModel?

    @Query("DELETE FROM itn_data")
    abstract fun deleteItn()

    //SNILS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSnils(snils: SnilsDbModel)

    @Query("SELECT * FROM snils_data WHERE internalId = :internalId")
    abstract fun getSnilsById(internalId: String): SnilsDbModel?

    @Query("SELECT * FROM snils_data WHERE userId = :userId")
    abstract fun getUserSnils(userId: String): SnilsDbModel?

    @Query("DELETE FROM snils_data")
    abstract fun deleteSnils()

    //passport
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPassport(passport: PassportDbModel)

    @Query("SELECT * FROM passport_data WHERE internalId = :internalId")
    abstract fun getPassportById(internalId: String): PassportDbModel?

    @Query("SELECT * FROM passport_data WHERE userId = :userId")
    abstract fun getUserPassport(userId: String): PassportDbModel?

    @Query("DELETE FROM passport_data")
    abstract fun deletePassport()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPassportPages(passportPages: List<PassportPageDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPassportPage(passportPage: PassportPageDbModel)

    @Query("SELECT * FROM passport_page_data WHERE userId = :userId")
    abstract fun getUserPassportPages(userId: String): List<PassportPageDbModel>

    @Query("DELETE FROM passport_page_data")
    abstract fun deletePassportPages()

    @Query("DELETE FROM passport_page_data WHERE page = :page")
    abstract fun deletePassportPage(page: Int)

    @Transaction
    open fun insertProfileData(
        profileDbModel: ProfileDbModel? = null,
        demographicsDbModel: DemographicsDbModel? = null,
        itnDbModel: ItnDbModel? = null,
        snilsDbModel: SnilsDbModel? = null,
        passportDbModel: PassportDbModel? = null,
        passportPageDbModels: List<PassportPageDbModel>? = null
    ) {
        profileDbModel?.let { insertProfile(it) }
        demographicsDbModel?.let { insertDemographics(it) }
        itnDbModel?.let { insertItn(it) }
        snilsDbModel?.let { insertSnils(it) }
        passportDbModel?.let { insertPassport(it) }
        passportPageDbModels?.let { insertPassportPages(it) }
    }

    @Transaction
    open fun deleteProfileData() {
        deleteProfile()
        deleteDemographics()
        deleteItn()
        deleteSnils()
        deletePassport()
        deletePassportPages()
    }
}