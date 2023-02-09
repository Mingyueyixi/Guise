package com.houvven.ktx_xposed.logger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ModuleLog::class], version = 1)
abstract class ModuleLogDBHelper : RoomDatabase() {

    abstract fun moduleLogDao(): ModuleLogDao

    companion object {

        private lateinit var db: ModuleLogDBHelper

        lateinit var moduleLogDao: ModuleLogDao

        fun init(context: Context) {
            db = Room
                .databaseBuilder(context, ModuleLogDBHelper::class.java, "module_log.db")
                .allowMainThreadQueries()
                .build()
            moduleLogDao = db.moduleLogDao()
        }
    }
}