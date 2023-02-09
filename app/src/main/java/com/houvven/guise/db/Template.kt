package com.houvven.guise.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.houvven.guise.util.android.Randoms
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@Entity
data class Template(
    @PrimaryKey val id: String = Randoms.uuidNoDash(),
    var name: String,
    var description: String? = null,
    var type: Int,
    var configuration: String,
    var createTime: Long = System.currentTimeMillis(),
    var updateTime: Long = System.currentTimeMillis(),
    var packageName: String? = null
) : java.io.Serializable {

    object Type {
        const val COMMON = 0
        const val EXCLUSIVE = 1
    }

    fun serialization(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        fun deserialization(json: String): Template {
            return Json.decodeFromString(serializer(), json)
        }
    }
}