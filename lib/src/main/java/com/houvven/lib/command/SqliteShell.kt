package com.houvven.lib.command


class SqliteShell private constructor(private val sqlite3: String) {

    private val shell = com.houvven.lib.command.ShellActuators

    @Throws(RuntimeException::class)
    private fun exec(db: String, sql: String) = arrayOf("$sqlite3 -json $db", sql).let {
        val result = shell.exec(it, true)
        if (result.isSuccess.not()) {
            throw RuntimeException(result.exceptionOrNull())
        }
        result.getOrNull()
    }

    @Throws(RuntimeException::class)
    private fun query(db: String, sql: String) = exec(db, sql)

    @Throws(RuntimeException::class)
    private fun <T> query(db: String, sql: String, mapper: (String) -> T) =
        query(db, sql)?.let { mapper(it) }

    @Throws(RuntimeException::class)
    private fun <T> query(db: String, sql: String, mapper: (String) -> T, default: T) =
        query(db, sql, mapper) ?: default


    @Throws(RuntimeException::class)
    private fun update(db: String, sql: String) = exec(db, sql)

    @Throws(RuntimeException::class)
    private fun delete(db: String, sql: String) = exec(db, sql)

    @Throws(RuntimeException::class)
    private fun insert(db: String, sql: String) = exec(db, sql)


    @Throws(RuntimeException::class)
    fun query(db: String, sql: String, vararg args: Any?) = query(db, sql.format(*args))

    @Throws(RuntimeException::class)
    fun <T> query(db: String, sql: String, mapper: (String) -> T, vararg args: Any?) =
        query(db, sql.format(*args), mapper)

    @Throws(RuntimeException::class)
    fun <T> query(db: String, sql: String, mapper: (String) -> T, default: T, vararg args: Any?) =
        query(db, sql.format(*args), mapper, default)


    @Throws(RuntimeException::class)
    fun update(db: String, sql: String, vararg args: Any?) = update(db, sql.format(*args))

    @Throws(RuntimeException::class)
    fun delete(db: String, sql: String, vararg args: Any?) = delete(db, sql.format(*args))

    @Throws(RuntimeException::class)
    fun insert(db: String, sql: String, vararg args: Any?) = insert(db, sql.format(*args))

    companion object {
        @JvmStatic
        fun of(sqlite3: String) = SqliteShell(sqlite3)
    }

}