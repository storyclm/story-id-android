package ru.breffi.storyid.profile.db

import androidx.room.*
import ru.breffi.storyid.profile.db.dto.BankAccountDbModel
import ru.breffi.storyid.profile.db.dto.FileDbModel

@Dao
abstract class FilesDataDao {

    @Query("DELETE FROM files_data")
    abstract fun deleteAll()

    @Query("DELETE FROM files_data WHERE id = :id")
    abstract fun deleteById(id: String)

    @Query("DELETE FROM files_data WHERE internalId = :internalId")
    abstract fun deleteByInternalId(internalId: String)

    @Query("SELECT * FROM files_data WHERE userId = :userId ORDER BY id ASC")
    abstract fun getUserFiles(userId: String): List<FileDbModel>

    @Query("SELECT * FROM files_data ORDER BY id ASC")
    abstract fun getAll(): List<FileDbModel>

    @Query("SELECT * FROM files_data WHERE internalId = :internalId")
    abstract fun getByInternalId(internalId: String): FileDbModel?

    @Query("SELECT * FROM files_data WHERE category = :category AND name = :name")
    abstract fun getByPath(category: String, name: String): FileDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(files: List<FileDbModel>)

    @Update
    abstract fun updateAll(files: List<FileDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(file: FileDbModel)

}