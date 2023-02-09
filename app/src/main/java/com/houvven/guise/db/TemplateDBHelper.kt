package com.houvven.guise.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.houvven.guise.ContextAmbient

@Database(entities = [Template::class], version = 3)
abstract class TemplateDBHelper : RoomDatabase() {

    abstract fun templateDao(): TemplateDao

    companion object {
        private val db: TemplateDBHelper by lazy {
            Room.databaseBuilder(
                ContextAmbient.current,
                TemplateDBHelper::class.java,
                "template.db"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }

        val templateDao by lazy { db.templateDao() }

    }
}