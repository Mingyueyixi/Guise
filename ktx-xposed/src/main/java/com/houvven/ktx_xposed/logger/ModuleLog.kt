package com.houvven.ktx_xposed.logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "module_log")
data class ModuleLog(
    // 自增id
    @PrimaryKey(autoGenerate = true) val id: Int? = null,

    // 日志时间
    @ColumnInfo(name = "time") val time: Long,
    // 日志类型
    @ColumnInfo(name = "type") val type: Char,
    // 日志来源
    @ColumnInfo(name = "source") val source: String,
    // 日志内容
    @ColumnInfo(name = "message") val message: String,
) {
    override fun toString(): String {
        return "[  ${String.format("%tF %<tT.%<tL", time)}      level:$type      source:$source  ]     $message"
    }
}
