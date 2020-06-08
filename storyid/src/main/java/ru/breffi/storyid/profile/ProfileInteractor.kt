package ru.breffi.storyid.profile

import ru.breffi.storyid.auth.common.AuthDataProvider
import ru.breffi.storyid.profile.api.ApiServiceProvider
import ru.breffi.storyid.profile.db.BankAccountDataDao
import ru.breffi.storyid.profile.db.FilesDataDao
import ru.breffi.storyid.profile.db.ProfileDataDao
import ru.breffi.storyid.profile.handler.BankAccountsHandler
import ru.breffi.storyid.profile.handler.FileHandler
import ru.breffi.storyid.profile.handler.ProfileHandler
import ru.breffi.storyid.profile.util.FileHelper
import ru.breffi.storyid.profile.util.withLocks

class ProfileInteractor internal constructor(
    apiServiceProvider: ApiServiceProvider,
    private val profileDataDao: ProfileDataDao,
    private val bankAccountDataDao: BankAccountDataDao,
    private val filesDataDao: FilesDataDao,
    private val authDataProvider: AuthDataProvider,
    private val fileHelper: FileHelper
) {

    val profileHandler = ProfileHandler(apiServiceProvider, profileDataDao, authDataProvider, fileHelper)
    val bankAccountsHandler = BankAccountsHandler(apiServiceProvider, bankAccountDataDao, authDataProvider)
    val fileHandler = FileHandler(apiServiceProvider, filesDataDao, authDataProvider, fileHelper)

    /**
     *  Blocking
     */
    fun removeUserData() {
        withLocks(profileHandler.getLock(), bankAccountsHandler.getLock(), fileHandler.getLock()) {
            authDataProvider.getAuthData()?.userId?.let { userId ->
                profileDataDao.deleteProfileData()
                bankAccountDataDao.deleteAll()
                filesDataDao.deleteAll()
                fileHelper.clearFiles()
            }
        }
    }
}