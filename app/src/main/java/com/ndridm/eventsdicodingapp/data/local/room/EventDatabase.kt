package com.ndridm.eventsdicodingapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null
        fun getDatabase(context: Context) : EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, EventDatabase::class.java, "event_database"
                ).addMigrations(Migration_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        private val Migration_1_2 = object : Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE favorite_news (id INTEGER PRIMARY KEY NOT NULL, name TEXT, mediaCover TEXT, beginTime TEXT"
                )

                db.execSQL(
                    "INSERT INTO favorite_news (id, name, beginTime, mediaCover) SELECT id, name, beginTime, mediaCover FROM favorite_events"
                )

                db.execSQL("ALTER TABLE favorite_news RENAME TO favorite")

                db.execSQL("DROP TABLE favorite")

            }

        }
    }
}