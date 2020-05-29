package ru.breffi.storyid.profile.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.breffi.storyid.profile.db.dto.*

@Database(
    entities = [
        ProfileDbModel::class, DemographicsDbModel::class, ItnDbModel::class, SnilsDbModel::class,
        BankAccountDbModel::class, PassportDbModel::class, PassportPageDbModel::class,
        //v2
        FileDbModel::class
    ], version = 2, exportSchema = false
)
abstract class StoryIdDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDataDao

    abstract fun bankAccountDao(): BankAccountDataDao

    abstract fun filesDao(): FilesDataDao

    companion object {

        private const val DATABASE_NAME = "story_id_database.db"

        fun build(context: Context): StoryIdDatabase {
            return Room.databaseBuilder(context, StoryIdDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
        }


        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS files_data (internalId TEXT NOT NULL," +
                            " id TEXT," +
                            " profileId TEXT," +
                            " createdAt INTEGER NOT NULL," +
                            " modifiedAt INTEGER NOT NULL," +
                            " uploaded INTEGER," +
                            " category TEXT NOT NULL," +
                            " name TEXT NOT NULL," +
                            " fileName TEXT," +
                            " size INTEGER," +
                            " description TEXT," +
                            " mimeType TEXT," +
                            " userId TEXT NOT NULL," +
                            " deleted INTEGER NOT NULL," +
                            " PRIMARY KEY(internalId))"
                )
            }
        }
    }
}