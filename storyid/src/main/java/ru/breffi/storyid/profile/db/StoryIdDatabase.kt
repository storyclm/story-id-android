package ru.breffi.storyid.profile.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.breffi.storyid.profile.db.dto.*

@Database(
    entities = [
        ProfileDbModel::class, DemographicsDbModel::class, ItnDbModel::class, SnilsDbModel::class,
        BankAccountDbModel::class, PassportDbModel::class, PassportPageDbModel::class
    ], version = 1, exportSchema = false
)
abstract class StoryIdDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDataDao

    abstract fun bankAccountDao(): BankAccountDataDao

    companion object {
        fun build(context: Context): StoryIdDatabase {
            return Room.databaseBuilder(context, StoryIdDatabase::class.java, DATABASE_NAME)
                .build()
        }

        private const val DATABASE_NAME = "story_id_database.db"
    }
}