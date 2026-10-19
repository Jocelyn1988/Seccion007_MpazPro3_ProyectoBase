package com.mpazpro3.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Rf05IntentosEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Rf05Database : RoomDatabase() {
    abstract fun intentosDao(): Rf05IntentosDao

    companion object {
        @Volatile private var instancia: Rf05Database? = null

        fun obtener(context: Context): Rf05Database =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    Rf05Database::class.java,
                    "rf05_intentos.db"
                ).build().also { instancia = it }
            }
    }
}