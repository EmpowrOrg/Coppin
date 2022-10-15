package org.empowrco.coppin.utils.files

import java.io.File
import java.io.FileWriter

interface FileUtil {
    fun writeToFile(prefix: String, suffix: String, currentDir: Boolean = false, writer: (FileWriter) -> Unit): File
}

object RealFileUtil : FileUtil {
    private fun createTempFile(prefix: String, suffix: String, currentDir: Boolean): File {
        val projectFile = if (currentDir) {
            val projectDir = System.getProperty("user.dir")
            File(projectDir)
        } else {
            null
        }

        val tempFile = File.createTempFile(prefix, suffix, projectFile)
        return projectFile?.let { tempFile.relativeToOrSelf(it) } ?: tempFile
    }

    override fun writeToFile(prefix: String, suffix: String, currentDir: Boolean, writer: (FileWriter) -> Unit): File {
        val tempFile = createTempFile(prefix, suffix, currentDir)
        val fileWriter = FileWriter(tempFile)
        fileWriter.use {
            writer(it)
        }
        return tempFile
    }
}
