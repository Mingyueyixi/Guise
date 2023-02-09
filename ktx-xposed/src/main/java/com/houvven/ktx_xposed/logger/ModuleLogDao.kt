package com.houvven.ktx_xposed.logger

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ModuleLogDao {

    @Query("SELECT * FROM module_log")
    fun getAll(): List<ModuleLog>

    @Insert
    fun insert(moduleLog: ModuleLog)

    @Query("DELETE FROM module_log")
    fun clearAll()

}