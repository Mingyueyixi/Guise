package com.houvven.guise.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TemplateDao {

    @Query("SELECT * FROM Template")
    fun getAll(): List<Template>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(template: Template)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMany(templates: List<Template>): List<Long>

    @Update
    fun update(template: Template)

    @Delete
    fun delete(template: Template)

    @Delete
    fun deleteMany(templates: List<Template>)

    @Delete
    fun deleteMany(vararg templates: Template)
}