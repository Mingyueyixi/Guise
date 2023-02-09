package com.houvven.lib.command

import java.io.DataOutputStream

object ShellActuators {

    private const val COMMAND_SU = "su"
    private const val COMMAND_SH = "sh"
    private const val COMMAND_EXIT = "exit\n"
    private const val COMMAND_LINE_END = "\n"


    @JvmStatic
    fun checkRoot(): Boolean = exec(arrayOf(""), true).isSuccess

    @JvmStatic
    fun exec(command: String, uesRoot: Boolean) = exec(arrayOf(command), uesRoot)

    @JvmStatic
    fun exec(
        commands: Array<String>,
        useRoot: Boolean
    ) = runCatching {
        if (commands.isEmpty()) {
            throw IllegalArgumentException("commands is empty.")
        }

        val process = Runtime.getRuntime().exec(if (useRoot) COMMAND_SU else COMMAND_SH)
        DataOutputStream(process.outputStream).use { os ->
            for (it in commands) {
                if (it.isBlank()) continue
                os.run {
                    write(it.toByteArray()); writeBytes(COMMAND_LINE_END); flush()
                }
            }
            os.writeBytes(COMMAND_EXIT)
            os.flush()
        }
        when (process.waitFor()) {
            0 -> process.inputStream.bufferedReader().readText()
            else -> throw RuntimeException(process.errorStream.bufferedReader().readText())
        }
    }
}
